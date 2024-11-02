package teamnova.elite_gear.model;

import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OrderDTO {

    private UUID orderID;

    @Size(max = 255)
    private String orderDate;

    @Size(max = 255)
    private String totalAmount;

    @Size(max = 255)
    private String status;

    private UUID customer;

    @OrderPaymentUnique
    private UUID payment;

    @OrderShippingUnique
    private UUID shipping;

}
