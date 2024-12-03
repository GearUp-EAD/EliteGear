package teamnova.elite_gear.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CategoryDTO {

    private UUID orderItemID;

    @NotNull
    @Size(max = 25)
    private String categoryName;

    private String imageUrl;

    private UUID parentCategoryID;

}
