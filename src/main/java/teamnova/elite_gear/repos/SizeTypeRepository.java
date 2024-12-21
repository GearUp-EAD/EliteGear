package teamnova.elite_gear.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import teamnova.elite_gear.domain.SizeType;

import java.util.UUID;

public interface SizeTypeRepository extends JpaRepository<SizeType, UUID> {
}