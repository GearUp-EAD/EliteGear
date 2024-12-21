package teamnova.elite_gear.repos;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import teamnova.elite_gear.domain.Category;


public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findByParentCategoryIsNull();

    List<Category> findByParentCategory(Category parentCategory);
}
