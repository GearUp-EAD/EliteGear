package teamnova.elite_gear.service;

import org.springframework.stereotype.Service;
import teamnova.elite_gear.domain.Order;
import teamnova.elite_gear.domain.OrderItem;
import teamnova.elite_gear.model.OrderDTO;
import teamnova.elite_gear.model.OrderItemDTO;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConvertTo_orderDTO_orderDTOs {
    public OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderID(order.getOrderID());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus().name());
        dto.setCustomerId(order.getCustomer().getCustomerID());

        Set<OrderItemDTO> itemDTOs = order.getOrderItems().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toSet());
        dto.setOrderItems(itemDTOs);

        return dto;
    }

    public static List<OrderDTO> convertToDTOs(List<Order> orders){
        return orders.stream()
                .map(order -> {
                    ConvertTo_orderDTO_orderDTOs converter = new ConvertTo_orderDTO_orderDTOs();
                    return converter.convertToDTO(order);
                })
                .collect(Collectors.toList());
    }


    public OrderItemDTO convertToDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setOrderItemID(item.getOrderItemID());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalPrice(item.getTotalPrice());
        dto.setProductVariantId(item.getProductVariant().getVariantId());
        return dto;
    }
}
