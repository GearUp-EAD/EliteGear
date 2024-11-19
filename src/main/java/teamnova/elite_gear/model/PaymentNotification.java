package teamnova.elite_gear.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaymentNotification {
    private String merchantId;
    private String order_id;
    private String payhereAmount;
    private String payhereCurrency;
    private String status_code;
    private String md5sig;

}