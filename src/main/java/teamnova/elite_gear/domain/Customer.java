package teamnova.elite_gear.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.apache.james.mime4j.dom.datetime.DateTime;
import org.hibernate.annotations.UuidGenerator;


@Entity
@Table(name = "Customers")
@Getter
@Setter
public class Customer {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue
    @UuidGenerator
    private UUID customerID;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String address;

    @Column( unique = true)
    private Integer phoneNumber;

    @Column
    private LocalDateTime createdAt;

    @Column
    private String imageUrl;

}
