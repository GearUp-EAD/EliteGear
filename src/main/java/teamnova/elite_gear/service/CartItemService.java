package teamnova.elite_gear.service;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import teamnova.elite_gear.domain.Cart;
import teamnova.elite_gear.domain.CartItem;
import teamnova.elite_gear.domain.Product;
import teamnova.elite_gear.model.CartItemDTO;
import teamnova.elite_gear.repos.CartItemRepository;
import teamnova.elite_gear.repos.CartRepository;
import teamnova.elite_gear.repos.ProductRepository;
import teamnova.elite_gear.util.NotFoundException;


@Service
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    public CartItemService(final CartItemRepository cartItemRepository,
                           final ProductRepository productRepository, final CartRepository cartRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
    }

    public List<CartItemDTO> findAll() {
        final List<CartItem> cartItems = cartItemRepository.findAll(Sort.by("cartItemID"));
        return cartItems.stream()
                .map(cartItem -> mapToDTO(cartItem, new CartItemDTO()))
                .toList();
    }

    public CartItemDTO get(final UUID cartItemID) {
        return cartItemRepository.findById(cartItemID)
                .map(cartItem -> mapToDTO(cartItem, new CartItemDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public UUID create(final CartItemDTO cartItemDTO) {
        final CartItem cartItem = new CartItem();
        mapToEntity(cartItemDTO, cartItem);
        return cartItemRepository.save(cartItem).getCartItemID();
    }

    public void update(final UUID cartItemID, final CartItemDTO cartItemDTO) {
        final CartItem cartItem = cartItemRepository.findById(cartItemID)
                .orElseThrow(NotFoundException::new);
        mapToEntity(cartItemDTO, cartItem);
        cartItemRepository.save(cartItem);
    }

    public void delete(final UUID cartItemID) {
        cartItemRepository.deleteById(cartItemID);
    }

    private CartItemDTO mapToDTO(final CartItem cartItem, final CartItemDTO cartItemDTO) {
        cartItemDTO.setCartItemID(cartItem.getCartItemID());
        cartItemDTO.setQuantity(cartItem.getQuantity());
        cartItemDTO.setProduct(cartItem.getProduct() == null ? null : cartItem.getProduct().getProductID());
        cartItemDTO.setCart(cartItem.getCart() == null ? null : cartItem.getCart().getCartID());
        return cartItemDTO;
    }

    private CartItem mapToEntity(final CartItemDTO cartItemDTO, final CartItem cartItem) {
        cartItem.setQuantity(cartItemDTO.getQuantity());
        final Product product = cartItemDTO.getProduct() == null ? null : productRepository.findById(cartItemDTO.getProduct())
                .orElseThrow(() -> new NotFoundException("product not found"));
        cartItem.setProduct(product);
        final Cart cart = cartItemDTO.getCart() == null ? null : cartRepository.findById(cartItemDTO.getCart())
                .orElseThrow(() -> new NotFoundException("cart not found"));
        cartItem.setCart(cart);
        return cartItem;
    }

}
