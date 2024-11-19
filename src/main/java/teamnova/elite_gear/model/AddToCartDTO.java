package teamnova.elite_gear.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AddToCartDTO {

    @NotNull
    private UUID customerID;
    @NotNull
    private UUID productID;
    @NotNull
    private Integer quantity;
}
