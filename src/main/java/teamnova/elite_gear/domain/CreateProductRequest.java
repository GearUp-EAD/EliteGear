package teamnova.elite_gear.domain;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateProductRequest {
    private String name;
    private String description;
    private Integer basePrice;
    private UUID categoryId;
    private String imageUrl;
    private List<CreateVariantRequest> variants;
}