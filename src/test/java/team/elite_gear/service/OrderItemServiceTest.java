package team.elite_gear.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

import teamnova.elite_gear.domain.Order;
import teamnova.elite_gear.domain.OrderItem;
import teamnova.elite_gear.domain.Product;
import teamnova.elite_gear.domain.ProductVariant;
import teamnova.elite_gear.model.OrderItemDTO;
import teamnova.elite_gear.repos.OrderItemRepository;
import teamnova.elite_gear.repos.OrderRepository;
import teamnova.elite_gear.repos.ProductRepository;
import teamnova.elite_gear.service.OrderItemService;
import teamnova.elite_gear.util.NotFoundException;

import java.util.*;

public class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderItemService orderItemService;

    private OrderItem orderItem;
    private OrderItemDTO orderItemDTO;
    private UUID orderItemID;
    private UUID orderID;
    private UUID productVariantId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        orderItemID = UUID.randomUUID();
        orderID = UUID.randomUUID();
        productVariantId = UUID.randomUUID();

        orderItem = new OrderItem();
        orderItem.setOrderItemID(orderItemID);
        orderItem.setQuantity(2);
        orderItem.setUnitPrice(100);

        Order order = new Order();
        order.setOrderID(orderID);
        orderItem.setOrder(order);

        ProductVariant productVariant = new ProductVariant();
        productVariant.setVariantId(productVariantId);
        orderItem.setProductVariant(productVariant);

        orderItemDTO = new OrderItemDTO();
        orderItemDTO.setOrderItemID(orderItemID);
        orderItemDTO.setQuantity(2);
        orderItemDTO.setUnitPrice(100);
        orderItemDTO.setProductVariantId(productVariantId);
    }

    @Test
    void testFindAll() {
        when(orderItemRepository.findAll(Sort.by("orderItemID"))).thenReturn(List.of(orderItem));

        List<OrderItemDTO> result = orderItemService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderItemID, result.get(0).getOrderItemID());
        verify(orderItemRepository).findAll(Sort.by("orderItemID"));
    }

    @Test
    void testGet() {
        when(orderItemRepository.findById(orderItemID)).thenReturn(Optional.of(orderItem));

        OrderItemDTO result = orderItemService.get(orderItemID);

        assertNotNull(result);
        assertEquals(orderItemID, result.getOrderItemID());
        verify(orderItemRepository).findById(orderItemID);
    }

    @Test
    void testGetNotFound() {
        when(orderItemRepository.findById(orderItemID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderItemService.get(orderItemID));
        verify(orderItemRepository).findById(orderItemID);
    }

//    @Test
//    void testCreate() {
//        Product product = new Product();
//        product.setProductID(productVariantId);
//        product.setBasePrice(100);
//
//        when(productRepository.findById(productVariantId)).thenReturn(Optional.of(product));
//        when(orderRepository.findById(orderID)).thenReturn(Optional.of(new Order()));
//        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> {
//            OrderItem savedOrderItem = invocation.getArgument(0);
//            savedOrderItem.setOrderItemID(orderItemID);
//            return savedOrderItem;
//        });
//
//        UUID result = orderItemService.create(orderItemDTO, orderID);
//
//        assertNotNull(result);
//        assertEquals(orderItemID, result);
//        verify(productRepository).findById(productVariantId);
//        verify(orderRepository).findById(orderID);
//        verify(orderItemRepository).save(any(OrderItem.class));
//    }
//
//    @Test
//    void testUpdate() {
//        when(orderItemRepository.findById(orderItemID)).thenReturn(Optional.of(orderItem));
//
//        orderItemService.update(orderItemID, orderItemDTO);
//
//        verify(orderItemRepository).findById(orderItemID);
//        verify(orderItemRepository).save(any(OrderItem.class));
//    }

    @Test
    void testUpdateNotFound() {
        when(orderItemRepository.findById(orderItemID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderItemService.update(orderItemID, orderItemDTO));
        verify(orderItemRepository).findById(orderItemID);
    }

    @Test
    void testDelete() {
        orderItemService.delete(orderItemID);

        verify(orderItemRepository).deleteById(orderItemID);
    }

    @Test
    void testFindMostSellingProducts() {
        Object[] product1 = {UUID.randomUUID(), 50};
        Object[] product2 = {UUID.randomUUID(), 30};

        when(orderItemRepository.findMostSellingProducts()).thenReturn(List.of(product1, product2));

        List<Object[]> result = orderItemService.findMostSellingProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderItemRepository, times(1)).findMostSellingProducts();
    }

}
