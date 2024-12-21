package teamnova.elite_gear.model;


import lombok.Data;
import java.util.UUID;

@Data
public class SizeDTO {
    private UUID sizeId;
    private String value;
    private UUID sizeTypeId;
}