package team.elite_gear.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import teamnova.elite_gear.model.CreateOrderDTO;
import teamnova.elite_gear.model.OrderDTO;
import teamnova.elite_gear.rest.OrderResource;
import teamnova.elite_gear.service.OrderService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderResourceTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderResource orderResource;

    private OrderDTO orderDTO;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // No-op implementation of Spring Validator
        org.springframework.validation.Validator noOpValidator = new org.springframework.validation.Validator() {
            @Override
            public boolean supports(Class<?> clazz) {
                return true; // Accept all classes for simplicity
            }

            @Override
            public void validate(Object target, org.springframework.validation.Errors errors) {
                // No validation performed
            }
        };

        mockMvc = MockMvcBuilders
                .standaloneSetup(orderResource)
                .setValidator(noOpValidator)
                .build();

        objectMapper = new ObjectMapper();
        orderId = UUID.randomUUID();
        orderDTO = new OrderDTO();
        orderDTO.setOrderID(orderId);
        orderDTO.setOrderDate(LocalDateTime.now());
        orderDTO.setTotalAmount(200);
        orderDTO.setStatus("PENDING");
        orderDTO.setCustomerId(UUID.randomUUID());
        orderDTO.setOrderItems(new HashSet<>());
    }

    @Test
    void testGetAllOrders() throws Exception {
        when(orderService.getAllOrders()).thenReturn(Collections.singletonList(orderDTO));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].orderID").value(orderId.toString()))
                .andExpect(jsonPath("$[0].customerId").value(orderDTO.getCustomerId().toString()))
                .andExpect(jsonPath("$[0].totalAmount").value(200))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void testGetOrder() throws Exception {
        when(orderService.getOrderById(orderId)).thenReturn(orderDTO);

        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderID").value(orderId.toString()))
                .andExpect(jsonPath("$.customerId").value(orderDTO.getCustomerId().toString()))
                .andExpect(jsonPath("$.totalAmount").value(200))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testCreateOrder() throws Exception {
        CreateOrderDTO createOrderDTO = new CreateOrderDTO();
        createOrderDTO.setCustomerId(orderDTO.getCustomerId());
        createOrderDTO.setItems(new HashSet<>());

        when(orderService.createOrders(any(CreateOrderDTO.class))).thenReturn(orderId);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(orderId.toString()));
    }

    @Test
    void testUpdateOrderStatus() throws Exception {
        String status = "SHIPPED";
        orderDTO.setStatus(status);

        when(orderService.updateOrderStatus(orderId, status)).thenReturn(orderDTO);

        mockMvc.perform(patch("/api/orders/{orderId}/status", orderId)
                        .param("status", status))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderID").value(orderId.toString()))
                .andExpect(jsonPath("$.status").value(status));
    }

    @Test
    void testDeleteOrder() throws Exception {
        mockMvc.perform(delete("/api/orders/{orderId}", orderId))
                .andExpect(status().isNoContent());

        verify(orderService).deleteOrder(orderId);
    }
}
