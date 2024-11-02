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
import teamnova.elite_gear.util.ReferencedWarning;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final ShippingRepository shippingRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(final OrderRepository orderRepository,
            final CustomerRepository customerRepository, final PaymentRepository paymentRepository,
            final ShippingRepository shippingRepository,
            final OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.shippingRepository = shippingRepository;
        this.orderItemRepository = orderItemRepository;
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
        mapToEntity(orderDTO, order);
        return orderRepository.save(order).getOrderID();
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
        orderDTO.setPayment(order.getPayment() == null ? null : order.getPayment().getId());
        orderDTO.setShipping(order.getShipping() == null ? null : order.getShipping().getShippingID());
        return orderDTO;
    }

    private Order mapToEntity(final OrderDTO orderDTO, final Order order) {
        order.setOrderDate(orderDTO.getOrderDate());
        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setStatus(orderDTO.getStatus());
        final Customer customer = orderDTO.getCustomer() == null ? null : customerRepository.findById(orderDTO.getCustomer())
                .orElseThrow(() -> new NotFoundException("customer not found"));
        order.setCustomer(customer);
        final Payment payment = orderDTO.getPayment() == null ? null : paymentRepository.findById(orderDTO.getPayment())
                .orElseThrow(() -> new NotFoundException("payment not found"));
        order.setPayment(payment);
        final Shipping shipping = orderDTO.getShipping() == null ? null : shippingRepository.findById(orderDTO.getShipping())
                .orElseThrow(() -> new NotFoundException("shipping not found"));
        order.setShipping(shipping);
        return order;
    }

    public boolean paymentExists(final UUID id) {
        return orderRepository.existsByPaymentId(id);
    }

    public boolean shippingExists(final UUID shippingID) {
        return orderRepository.existsByShippingShippingID(shippingID);
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
        return null;
    }

}
