package teamnova.elite_gear.model;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class CreateOrderDTO {
    private UUID customerId;
    private Set<CreateOrderItemDTO> items;
}

