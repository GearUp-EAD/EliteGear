package teamnova.elite_gear.service;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import teamnova.elite_gear.domain.Customer;
import teamnova.elite_gear.domain.Order;
import teamnova.elite_gear.domain.OrderItem;
import teamnova.elite_gear.domain.Payment;
import teamnova.elite_gear.domain.Shipping;
import teamnova.elite_gear.model.OrderDTO;
import teamnova.elite_gear.repos.CustomerRepository;
import teamnova.elite_gear.repos.OrderItemRepository;
import teamnova.elite_gear.repos.OrderRepository;
import teamnova.elite_gear.repos.PaymentRepository;
import teamnova.elite_gear.repos.ShippingRepository;
import teamnova.elite_gear.util.NotFoundException;
import teamnova.elite_gear.util.OrderStatus;
import teamnova.elite_gear.util.ReferencedWarning;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final ShippingRepository shippingRepository;

    public OrderService(final OrderRepository orderRepository,
                        final CustomerRepository customerRepository,
                        final OrderItemRepository orderItemRepository,
                        final PaymentRepository paymentRepository,
                        final ShippingRepository shippingRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.shippingRepository = shippingRepository;
    }

    public List<OrderDTO> findAll() {
        final List<Order> orders = orderRepository.findAll(Sort.by("orderID"));
        return orders.stream()
                .map(order -> mapToDTO(order, new OrderDTO()))
                .toList();
    }

    public OrderDTO get(final UUID orderID) {
        return orderRepository.findById(orderID)
                .map(order -> mapToDTO(order, new OrderDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public UUID create(final OrderDTO orderDTO) {
        final Order order = new Order();
        System.out.println(orderDTO.getCustomer());
        mapToEntity(orderDTO, order);
        return orderRepository.save(order).getOrderID();
    }

    public UUID createOrCheckOrder(final UUID customerID, Integer Sum) {
//        try {
//            Order order = orderRepository.findById(customerID)
//                    .orElseThrow(() -> new NotFoundException("Order not found"));
//            order.getCustomer().getCustomerID();
//            return order.getOrderID();
//        }
//        catch (NotFoundException e){
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setCustomer(customerID);
            orderDTO.setOrderDate( java.time.LocalDate.now().toString());
            orderDTO.setStatus(OrderStatus.PENDING);
            orderDTO.setTotalAmount(Sum);
            return create(orderDTO);

//        }
    }

    public void update(final UUID orderID, final OrderDTO orderDTO) {
        final Order order = orderRepository.findById(orderID)
                .orElseThrow(NotFoundException::new);
        mapToEntity(orderDTO, order);
        orderRepository.save(order);
    }

    public void delete(final UUID orderID) {
        orderRepository.deleteById(orderID);
    }

    private OrderDTO mapToDTO(final Order order, final OrderDTO orderDTO) {
        orderDTO.setOrderID(order.getOrderID());
        orderDTO.setOrderDate(order.getOrderDate());
        orderDTO.setTotalAmount(order.getTotalAmount());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setCustomer(order.getCustomer() == null ? null : order.getCustomer().getCustomerID());
        return orderDTO;
    }

    private Order mapToEntity(final OrderDTO orderDTO, final Order order) {
        order.setOrderDate(orderDTO.getOrderDate());
        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setStatus(orderDTO.getStatus());
        final Customer customer = orderDTO.getCustomer() == null ? null : customerRepository.findById(orderDTO.getCustomer())
                .orElseThrow(() -> new NotFoundException("customer not found"));
        order.setCustomer(customer);
        return order;
    }

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

}
