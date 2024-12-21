package teamnova.elite_gear.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import teamnova.elite_gear.domain.Product;
import teamnova.elite_gear.domain.ProductVariant;

import java.util.List;
import java.util.UUID;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {
    ProductVariant findFirstByProduct(Product product);

    List<ProductVariant> findByProductProductID(UUID productId);


}
