package teamnova.elite_gear.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentStatus {
    private String status;

    public PaymentStatus(String status) {
        this.status = status;
    }

    // Getters and setters
}