package teamnova.elite_gear.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter
@Setter
public class ProductVariantDTO {
    private UUID variantId;
    private String size;
    private Integer stockQuantity;
    private Integer priceAdjustment;
}