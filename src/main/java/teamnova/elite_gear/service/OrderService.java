package teamnova.elite_gear.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import teamnova.elite_gear.domain.*;
import teamnova.elite_gear.model.CreateOrderDTO;
import teamnova.elite_gear.model.CreateOrderItemDTO;
import teamnova.elite_gear.model.OrderDTO;
import teamnova.elite_gear.model.OrderItemDTO;
import teamnova.elite_gear.repos.CustomerRepository;
import teamnova.elite_gear.repos.OrderItemRepository;
import teamnova.elite_gear.repos.OrderRepository;
import teamnova.elite_gear.repos.PaymentRepository;
import teamnova.elite_gear.repos.ShippingRepository;
import teamnova.elite_gear.util.NotFoundException;
import teamnova.elite_gear.util.OrderStatus;
import teamnova.elite_gear.util.ReferencedWarning;
import teamnova.elite_gear.repos.ProductVariantRepository;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final ShippingRepository shippingRepository;
    private final ProductVariantRepository productVariantRepository;

    public OrderService(final OrderRepository orderRepository,
                        final CustomerRepository customerRepository,
                        final OrderItemRepository orderItemRepository,
                        final PaymentRepository paymentRepository,
                        final ShippingRepository shippingRepository,
                        final ProductVariantRepository productVariantRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.shippingRepository = shippingRepository;
        this.productVariantRepository = productVariantRepository;
    }

//    public List<OrderDTO> findAll() {
//        final List<Order> orders = orderRepository.findAll(Sort.by("orderID"));
//        return orders.stream()
//                .map(order -> mapToDTO(order, new OrderDTO()))
//                .toList();
//    }

//    public OrderDTO get(final UUID orderID) {
//        return orderRepository.findById(orderID)
//                .map(order -> mapToDTO(order, new OrderDTO()))
//                .orElseThrow(NotFoundException::new);
//    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToDTO(order);
    }

    public List<OrderDTO> getOrdersByCustomerId(UUID customerId) {
        return orderRepository.findAllByCustomer_CustomerID(customerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<OrderDTO> createOrders(List<CreateOrderDTO> createOrderDTOs) {
        List<OrderDTO> orderDTOs = new ArrayList<>();

        for (CreateOrderDTO createOrderDTO : createOrderDTOs) {
            // Process each order individually
            Customer customer = customerRepository.findById(createOrderDTO.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            Order order = new Order();
            order.setCustomer(customer);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus(OrderStatus.PENDING);
            order.setOrderItems(new HashSet<>());

            int totalAmount = 0;

            for (CreateOrderItemDTO itemDTO : createOrderDTO.getItems()) {
                ProductVariant variant = productVariantRepository.findById(itemDTO.getProductVariantId())
                        .orElseThrow(() -> new RuntimeException("Product variant not found"));

                // Check stock
                if (variant.getStockQuantity() < itemDTO.getQuantity()) {
                    throw new RuntimeException("Insufficient stock for product variant: " + variant.getVariantId());
                }

                // Calculate price
                int unitPrice = variant.getProduct().getBasePrice();
                if (variant.getPriceAdjustment() != null) {
                    unitPrice += variant.getPriceAdjustment();
                }

                OrderItem orderItem = new OrderItem();
                orderItem.setProductVariant(variant);
                orderItem.setQuantity(itemDTO.getQuantity());
                orderItem.setUnitPrice(unitPrice);
                order.addOrderItem(orderItem);

                // Update stock
                variant.setStockQuantity(variant.getStockQuantity() - itemDTO.getQuantity());
                totalAmount += unitPrice * itemDTO.getQuantity();
            }

            order.setTotalAmount(totalAmount);
            Order savedOrder = orderRepository.save(order);

            // Convert to DTO and add to the list
            orderDTOs.add(convertToDTO(savedOrder));
        }

        return orderDTOs;
    }


    @Transactional
    public OrderDTO updateOrderStatus(UUID orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.valueOf(newStatus));
        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Transactional
    public void deleteOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Restore stock quantities
        for (OrderItem item : order.getOrderItems()) {
            ProductVariant variant = item.getProductVariant();
            variant.setStockQuantity(variant.getStockQuantity() + item.getQuantity());
            productVariantRepository.save(variant);
        }

        orderRepository.delete(order);
    }

//    public UUID create(final OrderDTO orderDTO) {
//        final Order order = new Order();
//        System.out.println(orderDTO.getCustomer());
//        mapToEntity(orderDTO, order);
//        return orderRepository.save(order).getOrderID();
//    }

//    public UUID createOrCheckOrder(final UUID customerID, Integer Sum) {
////        try {
////            Order order = orderRepository.findById(customerID)
////                    .orElseThrow(() -> new NotFoundException("Order not found"));
////            order.getCustomer().getCustomerID();
////            return order.getOrderID();
////        }
////        catch (NotFoundException e){
//            OrderDTO orderDTO = new OrderDTO();
//            orderDTO.setCustomer(customerID);
//            orderDTO.setOrderDate( java.time.LocalDate.now().toString());
//            orderDTO.setStatus(OrderStatus.PENDING);
//            orderDTO.setTotalAmount(Sum);
//            return create(orderDTO);
//
////        }
//    }

//    public void update(final UUID orderID, final OrderDTO orderDTO) {
//        final Order order = orderRepository.findById(orderID)
//                .orElseThrow(NotFoundException::new);
//        mapToEntity(orderDTO, order);
//        orderRepository.save(order);
//    }

    public void delete(final UUID orderID) {
        orderRepository.deleteById(orderID);
    }

//    private OrderDTO mapToDTO(final Order order, final OrderDTO orderDTO) {
//        orderDTO.setOrderID(order.getOrderID());
//        orderDTO.setOrderDate(order.getOrderDate());
//        orderDTO.setTotalAmount(order.getTotalAmount());
//        orderDTO.setStatus(order.getStatus());
//        orderDTO.setCustomer(order.getCustomer() == null ? null : order.getCustomer().getCustomerID());
//        return orderDTO;
//    }

//    private Order mapToEntity(final OrderDTO orderDTO, final Order order) {
//        order.setOrderDate(orderDTO.getOrderDate());
//        order.setTotalAmount(orderDTO.getTotalAmount());
//        order.setStatus(orderDTO.getStatus());
//        final Customer customer = orderDTO.getCustomer() == null ? null : customerRepository.findById(orderDTO.getCustomer())
//                .orElseThrow(() -> new NotFoundException("customer not found"));
//        order.setCustomer(customer);
//        return order;
//    }

    public ReferencedWarning getReferencedWarning(final UUID orderID) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Order order = orderRepository.findById(orderID)
                .orElseThrow(NotFoundException::new);
        final OrderItem orderOrderItem = orderItemRepository.findFirstByOrder(order);
        if (orderOrderItem != null) {
            referencedWarning.setKey("order.orderItem.order.referenced");
            referencedWarning.addParam(orderOrderItem.getOrderItemID());
            return referencedWarning;
        }
        final Payment orderPayment = paymentRepository.findFirstByOrder(order);
        if (orderPayment != null) {
            referencedWarning.setKey("order.payment.order.referenced");
            referencedWarning.addParam(orderPayment.getId());
            return referencedWarning;
        }
        final Shipping orderShipping = shippingRepository.findFirstByOrder(order);
        if (orderShipping != null) {
            referencedWarning.setKey("order.shipping.order.referenced");
            referencedWarning.addParam(orderShipping.getShippingID());
            return referencedWarning;
        }
        return null;
    }
    private OrderDTO convertToDTO(Order order) {
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
    private OrderItemDTO convertToDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setOrderItemID(item.getOrderItemID());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalPrice(item.getTotalPrice());
        dto.setProductVariantId(item.getProductVariant().getVariantId());
        return dto;
    }


}
