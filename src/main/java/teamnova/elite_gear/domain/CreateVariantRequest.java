package teamnova.elite_gear.domain;

import lombok.Data;

import java.util.UUID;


@Data
public class CreateVariantRequest {
    private UUID sizeId;
    private Integer stockQuantity;
    private Integer priceAdjustment;
}