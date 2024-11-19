package teamnova.elite_gear.model;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CartItemDTO {

    private UUID cartItemID;

    @NotNull
    private Integer quantity;

    private UUID product;

    private UUID cart;

}
