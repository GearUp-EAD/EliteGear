package teamnova.elite_gear.model;


import lombok.Data;

import java.util.UUID;

@Data
public class CreateOrderItemDTO {
    private UUID productVariantId;
    private Integer quantity;
}