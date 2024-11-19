package teamnova.elite_gear.rest;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
import teamnova.elite_gear.model.OrderItemDTO;
import teamnova.elite_gear.service.OrderItemService;


@RestController
@RequestMapping(value = "/api/orderItems", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderItemResource {

    private final OrderItemService orderItemService;

    public OrderItemResource(final OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping
    public ResponseEntity<List<OrderItemDTO>> getAllOrderItems() {
        return ResponseEntity.ok(orderItemService.findAll());
    }

    @GetMapping("/{orderItemID}")
    public ResponseEntity<OrderItemDTO> getOrderItem(
            @PathVariable(name = "orderItemID") final UUID orderItemID) {
        return ResponseEntity.ok(orderItemService.get(orderItemID));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<List<UUID>> createOrderItems(
        @RequestBody @Valid final List<OrderItemDTO> orderItemDTOs) {
    List<UUID> createdOrderItemIDs = orderItemDTOs.stream()
            .map(orderItemService::create)
            .collect(Collectors.toList());
    return new ResponseEntity<>(createdOrderItemIDs, HttpStatus.CREATED);
}

    @PutMapping("/{orderItemID}")
    public ResponseEntity<UUID> updateOrderItem(
            @PathVariable(name = "orderItemID") final UUID orderItemID,
            @RequestBody @Valid final OrderItemDTO orderItemDTO) {
        orderItemService.update(orderItemID, orderItemDTO);
        return ResponseEntity.ok(orderItemID);
    }

    @DeleteMapping("/{orderItemID}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteOrderItem(
            @PathVariable(name = "orderItemID") final UUID orderItemID) {
        orderItemService.delete(orderItemID);
        return ResponseEntity.noContent().build();
    }

}