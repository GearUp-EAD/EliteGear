package teamnova.elite_gear.repos;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import teamnova.elite_gear.domain.Category;
import teamnova.elite_gear.domain.Product;


public interface ProductRepository extends JpaRepository<Product, UUID> {

    Product findFirstByCategory(Category category);

    @Query
    List<Product> findByCategoryOrderByName(Category category);

}
