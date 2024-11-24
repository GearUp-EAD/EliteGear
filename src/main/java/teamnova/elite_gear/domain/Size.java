package teamnova.elite_gear.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import teamnova.elite_gear.domain.Product;

import java.util.UUID;

@Entity
@Table(name = "Sizes")
@Getter
@Setter
public class Size {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue
    @UuidGenerator
    private UUID sizeId;

    @Column(nullable = false)
    private String value; // The actual size value (e.g., "L", "42", "9.5")

    @ManyToOne
    @JoinColumn(name = "size_type_id", nullable = false)
    private SizeType sizeType;
}