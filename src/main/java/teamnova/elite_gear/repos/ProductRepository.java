package teamnova.elite_gear.repos;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import teamnova.elite_gear.domain.Category;
import teamnova.elite_gear.domain.Product;


public interface ProductRepository extends JpaRepository<Product, UUID> {

    Product findFirstByCategory(Category category);

}
