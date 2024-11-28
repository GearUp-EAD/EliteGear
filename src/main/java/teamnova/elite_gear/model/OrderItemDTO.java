package teamnova.elite_gear.model;

import java.util.UUID;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Data
public class OrderItemDTO {
    private UUID orderItemID;
    private Integer quantity;
    private Integer unitPrice;
    private Integer totalPrice;
    private UUID productVariantId;
}
