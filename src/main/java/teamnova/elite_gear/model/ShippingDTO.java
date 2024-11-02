package teamnova.elite_gear.model;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ShippingDTO {

    private UUID shippingID;

    private LocalDate shippingDate;

    @Size(max = 150)
    private String shippingAddress;

    @Size(max = 40)
    private String shippingStatus;

}
