package teamnova.elite_gear.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import teamnova.elite_gear.domain.Size;

import java.util.List;
import java.util.UUID;

public interface SizeRepository extends JpaRepository<Size, UUID> {
    List<Size> findBySizeTypeSizeTypeId(UUID sizeTypeId);
}
