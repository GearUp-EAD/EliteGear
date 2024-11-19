package teamnova.elite_gear.repos;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import teamnova.elite_gear.domain.Customer;
import teamnova.elite_gear.domain.Order;


public interface OrderRepository extends JpaRepository<Order, UUID> {

    Order findFirstByCustomer(Customer customer);

}
