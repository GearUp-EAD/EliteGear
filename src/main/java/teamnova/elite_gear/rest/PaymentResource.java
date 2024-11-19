package teamnova.elite_gear.rest;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamnova.elite_gear.model.PaymentDTO;
import teamnova.elite_gear.model.PaymentNotification;
import teamnova.elite_gear.model.PaymentRequest;
import teamnova.elite_gear.model.PaymentResponse;
import teamnova.elite_gear.service.PaymentService;


@RestController
@RequestMapping(value = "/api/payments", produces = MediaType.APPLICATION_JSON_VALUE)
public class PaymentResource {

    private static final String MERCHANT_ID = "1228650";
    private static final String MERCHANT_SECRET = "MzczNDUzNDA5MDUyNzUwMDM0NjM0MDcwNTU2MTg3MjEzNDUwMA=="; // Replace with your Merchant Secret

    private final PaymentService paymentService;

    public PaymentResource(final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        return ResponseEntity.ok(paymentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPayment(@PathVariable(name = "id") final UUID id) {
        return ResponseEntity.ok(paymentService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<UUID> createPayment(@RequestBody @Valid final PaymentDTO paymentDTO) {
        final UUID createdId = paymentService.create(paymentDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }
    @PostMapping("/start")
    public PaymentResponse startPayment(@RequestBody PaymentRequest request) throws NoSuchAlgorithmException {
        String orderId = request.getOrder_id();
        String amount = request.getAmount();
        String currency = request.getCurrency();
        System.out.println("Received payment request for order: " + orderId + " amount: " + amount + " currency: " + currency);


        // Generate hash
        String hash = paymentService.generateHash(MERCHANT_ID + orderId + amount + currency +
                paymentService.generateHash(MERCHANT_SECRET).toUpperCase()).toUpperCase();



        return new PaymentResponse(MERCHANT_ID, hash);
    }

    @PostMapping(value = "/notify", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void paymentNotification(@ModelAttribute PaymentNotification notification, HttpServletResponse response) throws NoSuchAlgorithmException {
        try {
            // Add logging to see what data is coming in
            System.out.println("Received notification data: " + notification.getPayhere_amount() + notification.getPayhere_currency())  ;

            String localMd5Sig = paymentService.generateHash(
                    MERCHANT_ID +
                            notification.getOrder_id() +
                            notification.getPayhere_amount() +
                            notification.getPayhere_currency() +
                            notification.getStatus_code() +
                            paymentService.generateHash(MERCHANT_SECRET).toUpperCase()
            ).toUpperCase();


            if (localMd5Sig.equals(notification.getMd5sig()) && "2".equals(notification.getStatus_code())) {
                System.out.println("Payment successful for order: " + notification.getOrder_id());
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                System.out.println("Payment verification failed for order: " + notification.getOrder_id());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            System.out.println("Error processing payment notification: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UUID> updatePayment(@PathVariable(name = "id") final UUID id,
                                              @RequestBody @Valid final PaymentDTO paymentDTO) {
        paymentService.update(id, paymentDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deletePayment(@PathVariable(name = "id") final UUID id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
