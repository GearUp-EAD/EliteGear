package teamnova.elite_gear.repos;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import teamnova.elite_gear.domain.Shipping;


public interface ShippingRepository extends JpaRepository<Shipping, UUID> {
}
