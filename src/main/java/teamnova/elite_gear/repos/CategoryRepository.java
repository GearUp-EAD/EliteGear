package teamnova.elite_gear.repos;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import teamnova.elite_gear.domain.Category;


public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
