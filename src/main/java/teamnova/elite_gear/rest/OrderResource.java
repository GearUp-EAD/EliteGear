package teamnova.elite_gear.rest;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamnova.elite_gear.model.CreateOrderDTO;
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
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("oderCount/{customerId}")
    public ResponseEntity<Integer> getOrderCountByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(orderService.oderCount(customerId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomerId(customerId));
    }

    @GetMapping("/totalAmount/{customerId}")
    public ResponseEntity<Integer> getTotalAmountByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(orderService.getTotalAmount(customerId));
    }


    @PostMapping
    public ResponseEntity<List<OrderDTO>> createOrders(@RequestBody List<CreateOrderDTO> createOrderDTOs) {
        List<OrderDTO> orders = orderService.createOrders(createOrderDTOs);
        return ResponseEntity.ok(orders);
    }


    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
