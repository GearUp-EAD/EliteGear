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
import teamnova.elite_gear.model.OrderDTO;
import teamnova.elite_gear.service.OrderService;
import teamnova.elite_gear.util.ReferencedException;
import teamnova.elite_gear.util.ReferencedWarning;


@RestController
@RequestMapping(value = "/api/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderResource {

    private final OrderService orderService;

    public OrderResource(final OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{orderID}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable(name = "orderID") final UUID orderID) {
        return ResponseEntity.ok(orderService.get(orderID));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<UUID> createOrder(@RequestBody @Valid final OrderDTO orderDTO) {
        final UUID createdOrderID = orderService.create(orderDTO);
        return new ResponseEntity<>(createdOrderID, HttpStatus.CREATED);
    }

    @PutMapping("/{orderID}")
    public ResponseEntity<UUID> updateOrder(@PathVariable(name = "orderID") final UUID orderID,
                                            @RequestBody @Valid final OrderDTO orderDTO) {
        orderService.update(orderID, orderDTO);
        return ResponseEntity.ok(orderID);
    }

    @DeleteMapping("/{orderID}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteOrder(@PathVariable(name = "orderID") final UUID orderID) {
        final ReferencedWarning referencedWarning = orderService.getReferencedWarning(orderID);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        orderService.delete(orderID);
        return ResponseEntity.noContent().build();
    }

}
