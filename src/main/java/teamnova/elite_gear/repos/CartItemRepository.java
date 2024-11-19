package teamnova.elite_gear.repos;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import teamnova.elite_gear.domain.Cart;
import teamnova.elite_gear.domain.CartItem;
import teamnova.elite_gear.domain.Customer;
import teamnova.elite_gear.domain.Product;


public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    CartItem findFirstByProduct(Product product);

    CartItem findFirstByCart(Cart cart);




}
