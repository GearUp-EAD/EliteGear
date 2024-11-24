package teamnova.elite_gear.model;

import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import teamnova.elite_gear.util.OrderStatus;


@Getter
@Setter
public class OrderDTO {

    private UUID orderID;

    @Size(max = 255)
    private String orderDate;

    private Integer totalAmount;

    @Size(max = 255)
    private OrderStatus status;

    private UUID customer;

}
