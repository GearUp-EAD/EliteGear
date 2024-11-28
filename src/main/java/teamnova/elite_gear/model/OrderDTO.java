package teamnova.elite_gear.model;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import teamnova.elite_gear.util.OrderStatus;


@Getter
@Setter
public class OrderDTO {
    private UUID orderID;
    private LocalDateTime orderDate;
    private Integer totalAmount;
    private String status;
    private UUID customerId;
    private Set<OrderItemDTO> orderItems;
}
