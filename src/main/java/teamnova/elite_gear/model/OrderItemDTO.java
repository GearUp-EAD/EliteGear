package teamnova.elite_gear.model;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OrderItemDTO {

    private UUID orderItemID;
    private Integer quantity;
    private Integer price;
    private UUID order;
    private UUID product;

}
