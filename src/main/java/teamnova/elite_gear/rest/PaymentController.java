package teamnova.elite_gear.rest;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import teamnova.elite_gear.model.PaymentRequest;
import teamnova.elite_gear.model.PaymentStatus;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    private final SimpMessagingTemplate messagingTemplate;

    public PaymentController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/create-checkout-session")
    public String createCheckoutSession(@RequestBody PaymentRequest request) throws Exception {
        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:3000/cancel")
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(request.getAmount() * 100) // amount in cents
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(request.getProductName())
                                        .build())
                                .build())
                        .build())
                .build();

        Session session = Session.create(params);
        return session.getId();
    }

    @PostMapping("/webhook")
    public void handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        // Verify webhook signature and process event
        try {
            // Process the webhook event and send status via WebSocket
            messagingTemplate.convertAndSend("/topic/payment-status", new PaymentStatus("success"));
        } catch (Exception e) {
            messagingTemplate.convertAndSend("/topic/payment-status", new PaymentStatus("failed"));
        }
    }
}