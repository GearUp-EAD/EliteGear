package teamnova.elite_gear.repos;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import teamnova.elite_gear.domain.Order;
import teamnova.elite_gear.domain.Shipping;


public interface ShippingRepository extends JpaRepository<Shipping, UUID> {

    Shipping findFirstByOrder(Order order);

    boolean existsByOrderOrderID(UUID orderID);

}
