package teamnova.elite_gear.repos;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import teamnova.elite_gear.domain.Order;
import teamnova.elite_gear.domain.Shipping;


public interface ShippingRepository extends JpaRepository<Shipping, UUID> {

    Shipping findFirstByOrder(Order order);

    boolean existsByOrderOrderID(UUID orderID);

    Optional<Shipping> findByOrderOrderID(UUID orderID);

    @Modifying
    @Transactional
    @Query("UPDATE Shipping s SET s.shippingStatus = :status WHERE s.order.orderID = :orderID")
    void updateShippingStatusByOrderID(@Param("orderID") UUID orderID, @Param("status") String status);

}
