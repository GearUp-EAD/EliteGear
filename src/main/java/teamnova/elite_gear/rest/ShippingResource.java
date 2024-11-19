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
import teamnova.elite_gear.model.ShippingDTO;
import teamnova.elite_gear.service.ShippingService;


@RestController
@RequestMapping(value = "/api/shippings", produces = MediaType.APPLICATION_JSON_VALUE)
public class ShippingResource {

    private final ShippingService shippingService;

    public ShippingResource(final ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @GetMapping
    public ResponseEntity<List<ShippingDTO>> getAllShippings() {
        return ResponseEntity.ok(shippingService.findAll());
    }

    @GetMapping("/{shippingID}")
    public ResponseEntity<ShippingDTO> getShipping(
            @PathVariable(name = "shippingID") final UUID shippingID) {
        return ResponseEntity.ok(shippingService.get(shippingID));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<UUID> createShipping(@RequestBody @Valid final ShippingDTO shippingDTO) {
        final UUID createdShippingID = shippingService.create(shippingDTO);
        return new ResponseEntity<>(createdShippingID, HttpStatus.CREATED);
    }

    @PutMapping("/{shippingID}")
    public ResponseEntity<UUID> updateShipping(
            @PathVariable(name = "shippingID") final UUID shippingID,
            @RequestBody @Valid final ShippingDTO shippingDTO) {
        shippingService.update(shippingID, shippingDTO);
        return ResponseEntity.ok(shippingID);
    }

    @DeleteMapping("/{shippingID}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteShipping(
            @PathVariable(name = "shippingID") final UUID shippingID) {
        shippingService.delete(shippingID);
        return ResponseEntity.noContent().build();
    }

}
