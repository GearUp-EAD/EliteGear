package teamnova.elite_gear.domain;

import jakarta.persistence.*;
import teamnova.elite_gear.domain.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "Product_Variants")
@Getter
@Setter
public class ProductVariant {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue
    @UuidGenerator
    private UUID variantId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "size_id", nullable = false)
    private Size size;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Column
    private Integer priceAdjustment; // Additional price for this variant, if any
}
