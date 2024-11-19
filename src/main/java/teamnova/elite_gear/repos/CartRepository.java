package teamnova.elite_gear.repos;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import teamnova.elite_gear.domain.Cart;
import teamnova.elite_gear.domain.Customer;


public interface CartRepository extends JpaRepository<Cart, UUID> {

    Cart findFirstByCustomer(Customer customer);

    Optional <Cart> findByCustomerCustomerID(UUID customerID);

    boolean existsByCustomerCustomerID(UUID customerID);

}
