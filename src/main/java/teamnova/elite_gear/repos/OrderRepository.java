package teamnova.elite_gear.repos;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamnova.elite_gear.domain.Customer;
import teamnova.elite_gear.domain.Order;


public interface OrderRepository extends JpaRepository<Order, UUID> {

    Order findFirstByCustomer(Customer customer);

    List<Order> findAllByCustomer_CustomerID(UUID customerId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.customer.customerID = :customerId")
    Integer countByCustomerId(@Param("customerId") UUID customerId);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.customer.customerID = :customerId")
    Integer getTotalAmountByCustomerId(@Param("customerId") UUID customerId);



}
