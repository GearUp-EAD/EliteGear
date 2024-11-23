package teamnova.elite_gear.model;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CustomerOrderItemsDTO {
    private UUID customerID;
    private List<OrderItemDTO> orderItems;
}
