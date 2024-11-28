package teamnova.elite_gear.domain;

import jakarta.persistence.*;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;


@Entity
@Table(name = "OrderItems")
@Getter
@Setter
public class OrderItem {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue
    @UuidGenerator
    private UUID orderItemID;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer unitPrice;

    @Column(nullable = false)
    private Integer totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    // Helper method to calculate total price
    @PrePersist
    @PreUpdate
    private void calculateTotalPrice() {
        this.totalPrice = this.quantity * this.unitPrice;
    }
}
