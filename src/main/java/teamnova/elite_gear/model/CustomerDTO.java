package teamnova.elite_gear.model;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CustomerDTO {

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

    @Column
    private String imageUrl;

}
