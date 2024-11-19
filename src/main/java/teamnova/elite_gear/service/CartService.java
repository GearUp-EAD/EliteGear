package teamnova.elite_gear.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import teamnova.elite_gear.domain.Cart;
import teamnova.elite_gear.domain.CartItem;
import teamnova.elite_gear.domain.Customer;
import teamnova.elite_gear.domain.Product;
import teamnova.elite_gear.model.AddToCartDTO;
import teamnova.elite_gear.model.CartDTO;
import teamnova.elite_gear.repos.CartItemRepository;
import teamnova.elite_gear.repos.ProductRepository;
import teamnova.elite_gear.repos.CartRepository;
import teamnova.elite_gear.repos.CustomerRepository;
import teamnova.elite_gear.util.NotFoundException;
import teamnova.elite_gear.util.ReferencedWarning;


@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(final CartRepository cartRepository,
                       final CustomerRepository customerRepository,
                       final CartItemRepository cartItemRepository,
                       final ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public List<CartDTO> findAll() {
        final List<Cart> carts = cartRepository.findAll(Sort.by("cartID"));
        return carts.stream()
                .map(cart -> mapToDTO(cart, new CartDTO()))
                .toList();
    }

    public CartDTO get(final UUID cartID) {
        return cartRepository.findById(cartID)
                .map(cart -> mapToDTO(cart, new CartDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public UUID create(final CartDTO cartDTO) {
        final Cart cart = new Cart();
        mapToEntity(cartDTO, cart);
        return cartRepository.save(cart).getCartID();
    }

    public UUID findCartByCustomer(final UUID customerID) {
        final Customer customer = customerRepository.findById(customerID)
                .orElseThrow(NotFoundException::new);
        final Cart cart = cartRepository.findFirstByCustomer(customer);
        return cart.getCartID();
    }




    public void update(final UUID cartID, final CartDTO cartDTO) {
        final Cart cart = cartRepository.findById(cartID)
                .orElseThrow(NotFoundException::new);
        mapToEntity(cartDTO, cart);
        cartRepository.save(cart);
    }

    public void delete(final UUID cartID) {
        cartRepository.deleteById(cartID);
    }

    private CartDTO mapToDTO(final Cart cart, final CartDTO cartDTO) {
        cartDTO.setCartID(cart.getCartID());
        cartDTO.setCreateDate(cart.getCreateDate());
        cartDTO.setLastUpdated(cart.getLastUpdated());
        cartDTO.setCustomer(cart.getCustomer() == null ? null : cart.getCustomer().getCustomerID());
        return cartDTO;
    }

    private Cart mapToEntity(final CartDTO cartDTO, final Cart cart) {
        cart.setCreateDate(cartDTO.getCreateDate());
        cart.setLastUpdated(cartDTO.getLastUpdated());
        final Customer customer = cartDTO.getCustomer() == null ? null : customerRepository.findById(cartDTO.getCustomer())
                .orElseThrow(() -> new NotFoundException("customer not found"));
        cart.setCustomer(customer);
        return cart;
    }

    public boolean customerExists(final UUID customerID) {
        return cartRepository.existsByCustomerCustomerID(customerID);
    }

    public ReferencedWarning getReferencedWarning(final UUID cartID) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Cart cart = cartRepository.findById(cartID)
                .orElseThrow(NotFoundException::new);
        final CartItem cartCartItem = cartItemRepository.findFirstByCart(cart);
        if (cartCartItem != null) {
            referencedWarning.setKey("cart.cartItem.cart.referenced");
            referencedWarning.addParam(cartCartItem.getCartItemID());
            return referencedWarning;
        }
        return null;
    }

    public UUID addToCart(@Valid AddToCartDTO addToCartDTO) {
        UUID customerID = addToCartDTO.getCustomerID();

        // Retrieve customer first to ensure it exists
        Customer customer = customerRepository.findById(customerID)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        // Find or create cart
        Cart cart = cartRepository.findByCustomerCustomerID(customerID)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomer(customer);
                    newCart.setCreateDate(LocalDateTime.now());
                    newCart.setLastUpdated(LocalDateTime.now());
                    return cartRepository.save(newCart);
                });

        // Find product
        Product product = productRepository.findById(addToCartDTO.getProductID())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        // Create and save cart item
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(addToCartDTO.getQuantity());
        cartItemRepository.save(cartItem);

        return cart.getCartID();
    }
    }
