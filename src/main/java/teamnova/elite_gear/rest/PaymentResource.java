package teamnova.elite_gear.rest;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamnova.elite_gear.model.*;
import teamnova.elite_gear.service.PaymentService;


@RestController
@RequestMapping(value = "/api/payments", produces = MediaType.APPLICATION_JSON_VALUE)
public class PaymentResource {


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

    @GetMapping("/total")
    public ResponseEntity<Integer> getTotalPaymentAmount() {
        return ResponseEntity.ok(paymentService.getTotalPaymentAmount());
    }

    @GetMapping("/paymentSummary")
    public ResponseEntity<List<PaymentSummaryDTO>> getPaymentSummary() {
        return ResponseEntity.ok(paymentService.getPaymentSummary());
    }

    @GetMapping("/latestFiveTransactions")
    public ResponseEntity<List<Object[]>> getLatestFiveTransactions() {
        return ResponseEntity.ok(paymentService.getLatestFiveTransactions());
    }

    @GetMapping("/MonthlyPaymentGrowth")
    public ResponseEntity<List<Object[]>> getMonthlyPaymentGrowth() {
        return ResponseEntity.ok(paymentService.getMonthlyPaymentGrowth());
    }


    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<UUID> createPayment(@RequestBody @Valid final PaymentDTO paymentDTO) {
        Logger logger = LoggerFactory.getLogger(PaymentResource.class);
        final UUID createdId = paymentService.create(paymentDTO);
        logger.info("Created Payment ID: {}", createdId);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }


    @PostMapping("/start")
    @ApiResponse(responseCode = "200")
    public PaymentResponse startPayment(@RequestBody PaymentRequest request) throws NoSuchAlgorithmException {
        return paymentService.startPayment(request);
    }


    @PostMapping(value = "/notify", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ApiResponse(responseCode = "200")
    public ResponseEntity<UUID> paymentNotification(@ModelAttribute PaymentNotification notification) throws NoSuchAlgorithmException {

            UUID paymentId = paymentService.processPaymentNotification(notification);

        return ResponseEntity.status(HttpStatus.OK).body(paymentId);
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
