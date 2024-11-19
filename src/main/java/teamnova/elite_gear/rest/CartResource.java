package teamnova.elite_gear.rest;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamnova.elite_gear.model.AddToCartDTO;
import teamnova.elite_gear.model.CartDTO;
import teamnova.elite_gear.service.CartService;
import teamnova.elite_gear.util.ReferencedException;
import teamnova.elite_gear.util.ReferencedWarning;


@RestController
@RequestMapping(value = "/api/carts", produces = MediaType.APPLICATION_JSON_VALUE)
public class CartResource {

    private final CartService cartService;

    public CartResource(final CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<List<CartDTO>> getAllCarts() {
        return ResponseEntity.ok(cartService.findAll());
    }

    @GetMapping("/{cartID}")
    public ResponseEntity<CartDTO> getCart(@PathVariable(name = "cartID") final UUID cartID) {
        return ResponseEntity.ok(cartService.get(cartID));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<UUID> createCart(@RequestBody @Valid final CartDTO cartDTO) {
        final UUID createdCartID = cartService.create(cartDTO);
        return new ResponseEntity<>(createdCartID, HttpStatus.CREATED);
    }

    @PostMapping("/addToCart")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<UUID> addToCart(@RequestBody @Valid final AddToCartDTO addToCartDTO) {
        final UUID createdCartID = cartService.addToCart(addToCartDTO);
        return new ResponseEntity<>(createdCartID, HttpStatus.CREATED);
    }

    @PutMapping("/{cartID}")
    public ResponseEntity<UUID> updateCart(@PathVariable(name = "cartID") final UUID cartID,
                                           @RequestBody @Valid final CartDTO cartDTO) {
        cartService.update(cartID, cartDTO);
        return ResponseEntity.ok(cartID);
    }

    @DeleteMapping("/{cartID}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteCart(@PathVariable(name = "cartID") final UUID cartID) {
        final ReferencedWarning referencedWarning = cartService.getReferencedWarning(cartID);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        cartService.delete(cartID);
        return ResponseEntity.noContent().build();
    }

}
