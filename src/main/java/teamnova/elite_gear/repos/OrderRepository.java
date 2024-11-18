package teamnova.elite_gear.repos;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import teamnova.elite_gear.domain.Customer;
import teamnova.elite_gear.domain.Order;
import teamnova.elite_gear.domain.Shipping;


public interface OrderRepository extends JpaRepository<Order, UUID> {

    Order findFirstByCustomer(Customer customer);

    Order findFirstByShipping(Shipping shipping);

    boolean existsByShippingShippingID(UUID shippingID);

}
