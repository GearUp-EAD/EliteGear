package teamnova.elite_gear.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "Size_Types")
@Getter
@Setter
public class SizeType {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue
    @UuidGenerator
    private UUID sizeTypeId;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "Clothing", "Shoes", "Generic"

    @OneToMany(mappedBy = "sizeType")
    private Set<Size> sizes;
}
