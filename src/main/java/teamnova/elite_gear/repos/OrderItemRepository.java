package teamnova.elite_gear.repos;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import teamnova.elite_gear.domain.Order;
import teamnova.elite_gear.domain.OrderItem;
import teamnova.elite_gear.domain.Product;


public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    OrderItem findFirstByOrder(Order order);

        @Query("SELECT p.name, SUM(oi.quantity) AS totalSold " +
                "FROM OrderItem oi " +
                "JOIN oi.productVariant pv " +
                "JOIN pv.product p " +
                "GROUP BY p.name " +
                "ORDER BY totalSold DESC")
        List<Object[]> findMostSellingProducts();

}
