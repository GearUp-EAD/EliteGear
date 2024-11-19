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
import teamnova.elite_gear.model.CartItemDTO;
import teamnova.elite_gear.service.CartItemService;


@RestController
@RequestMapping(value = "/api/cartItems", produces = MediaType.APPLICATION_JSON_VALUE)
public class CartItemResource {

    private final CartItemService cartItemService;

    public CartItemResource(final CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @GetMapping
    public ResponseEntity<List<CartItemDTO>> getAllCartItems() {
        return ResponseEntity.ok(cartItemService.findAll());
    }

    @GetMapping("/{cartItemID}")
    public ResponseEntity<CartItemDTO> getCartItem(
            @PathVariable(name = "cartItemID") final UUID cartItemID) {
        return ResponseEntity.ok(cartItemService.get(cartItemID));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<UUID> createCartItem(@RequestBody @Valid final CartItemDTO cartItemDTO) {
        final UUID createdCartItemID = cartItemService.create(cartItemDTO);
        return new ResponseEntity<>(createdCartItemID, HttpStatus.CREATED);
    }

    @PutMapping("/{cartItemID}")
    public ResponseEntity<UUID> updateCartItem(
            @PathVariable(name = "cartItemID") final UUID cartItemID,
            @RequestBody @Valid final CartItemDTO cartItemDTO) {
        cartItemService.update(cartItemID, cartItemDTO);
        return ResponseEntity.ok(cartItemID);
    }

    @DeleteMapping("/{cartItemID}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteCartItem(
            @PathVariable(name = "cartItemID") final UUID cartItemID) {
        cartItemService.delete(cartItemID);
        return ResponseEntity.noContent().build();
    }

}
