package teamnova.elite_gear.model;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PaymentDTO {

    private UUID id;

    private LocalDate paymentDate;

    private Integer paymentAmount;

    @Size(max = 20)
    private String paymentMethod;

}
