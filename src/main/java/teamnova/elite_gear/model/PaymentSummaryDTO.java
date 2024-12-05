package teamnova.elite_gear.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentSummaryDTO {

    public PaymentSummaryDTO(int year, int month, long totalAmount) {
        this.year = year;
        this.month = month;
        this.totalAmount = totalAmount;
    }

    private int year;
    private int month;
    private long totalAmount;
}
