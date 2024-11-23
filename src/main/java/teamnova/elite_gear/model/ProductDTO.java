package teamnova.elite_gear.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProductDTO {

    private UUID productID;

    @NotNull
    @Size(max = 50)
    private String name;

    @NotNull
    @Size(max = 255)
    private String description;

    @NotNull
    private Integer price;

    @NotNull
    private Integer stockQuantity;

    private UUID category;

}
