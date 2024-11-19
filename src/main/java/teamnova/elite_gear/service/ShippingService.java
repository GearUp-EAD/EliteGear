package teamnova.elite_gear.service;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import teamnova.elite_gear.domain.Order;
import teamnova.elite_gear.domain.Shipping;
import teamnova.elite_gear.model.ShippingDTO;
import teamnova.elite_gear.repos.OrderRepository;
import teamnova.elite_gear.repos.ShippingRepository;
import teamnova.elite_gear.util.NotFoundException;


@Service
public class ShippingService {

    private final ShippingRepository shippingRepository;
    private final OrderRepository orderRepository;

    public ShippingService(final ShippingRepository shippingRepository,
                           final OrderRepository orderRepository) {
        this.shippingRepository = shippingRepository;
        this.orderRepository = orderRepository;
    }

    public List<ShippingDTO> findAll() {
        final List<Shipping> shippings = shippingRepository.findAll(Sort.by("shippingID"));
        return shippings.stream()
                .map(shipping -> mapToDTO(shipping, new ShippingDTO()))
                .toList();
    }

    public ShippingDTO get(final UUID shippingID) {
        return shippingRepository.findById(shippingID)
                .map(shipping -> mapToDTO(shipping, new ShippingDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public UUID create(final ShippingDTO shippingDTO) {
        final Shipping shipping = new Shipping();
        mapToEntity(shippingDTO, shipping);
        return shippingRepository.save(shipping).getShippingID();
    }

    public void update(final UUID shippingID, final ShippingDTO shippingDTO) {
        final Shipping shipping = shippingRepository.findById(shippingID)
                .orElseThrow(NotFoundException::new);
        mapToEntity(shippingDTO, shipping);
        shippingRepository.save(shipping);
    }

    public void delete(final UUID shippingID) {
        shippingRepository.deleteById(shippingID);
    }

    private ShippingDTO mapToDTO(final Shipping shipping, final ShippingDTO shippingDTO) {
        shippingDTO.setShippingID(shipping.getShippingID());
        shippingDTO.setShippingDate(shipping.getShippingDate());
        shippingDTO.setShippingAddress(shipping.getShippingAddress());
        shippingDTO.setShippingStatus(shipping.getShippingStatus());
        shippingDTO.setOrder(shipping.getOrder() == null ? null : shipping.getOrder().getOrderID());
        return shippingDTO;
    }

    private Shipping mapToEntity(final ShippingDTO shippingDTO, final Shipping shipping) {
        shipping.setShippingDate(shippingDTO.getShippingDate());
        shipping.setShippingAddress(shippingDTO.getShippingAddress());
        shipping.setShippingStatus(shippingDTO.getShippingStatus());
        final Order order = shippingDTO.getOrder() == null ? null : orderRepository.findById(shippingDTO.getOrder())
                .orElseThrow(() -> new NotFoundException("order not found"));
        shipping.setOrder(order);
        return shipping;
    }

    public boolean orderExists(final UUID orderID) {
        return shippingRepository.existsByOrderOrderID(orderID);
    }

}
