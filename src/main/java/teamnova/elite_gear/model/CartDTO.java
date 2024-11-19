package teamnova.elite_gear.model;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CartDTO {

    private UUID cartID;

    @NotNull
    private LocalDateTime createDate;

    @NotNull
    private LocalDateTime lastUpdated;

    @CartCustomerUnique
    private UUID customer;

}
