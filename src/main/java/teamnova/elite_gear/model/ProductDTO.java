package teamnova.elite_gear.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Data
public class ProductDTO {
    private UUID productId;
    private String name;
    private String description;
    private Integer basePrice;
    private String categoryName;
    private List<ProductVariantDTO> variants;
}
