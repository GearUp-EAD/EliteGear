package teamnova.elite_gear.repos;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import teamnova.elite_gear.domain.Order;
import teamnova.elite_gear.domain.OrderItem;
import teamnova.elite_gear.domain.Product;


public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    OrderItem findFirstByOrder(Order order);



}
