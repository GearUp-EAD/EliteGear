package teamnova.elite_gear.model;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class SizeTypeDTO {
    private UUID sizeTypeId;
    private String name;
    private Set<SizeDTO> sizes;
}
