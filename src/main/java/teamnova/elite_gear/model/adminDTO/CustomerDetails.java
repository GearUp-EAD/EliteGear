package teamnova.elite_gear.model.adminDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import teamnova.elite_gear.model.CustomerEmailUnique;
import teamnova.elite_gear.model.CustomerPhoneNumberUnique;


@Getter
@Setter
public class CustomerDetails {

    private UUID customerID;

    @NotNull
    @Size(max = 50)
    private String name;

    @NotNull
    @Size(max = 255)
    @CustomerEmailUnique
    private String email;

    @NotNull
    @Size(max = 255)
    private String address;

    @NotNull
    @CustomerPhoneNumberUnique
    private Integer phoneNumber;

    private String imageUrl;

    private Integer oderCount;

    private Integer totalSpent;


}
