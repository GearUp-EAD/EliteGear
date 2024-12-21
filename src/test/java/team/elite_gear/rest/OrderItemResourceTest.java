package team.elite_gear.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import teamnova.elite_gear.model.OrderItemDTO;
import teamnova.elite_gear.rest.OrderItemResource;
import teamnova.elite_gear.service.OrderItemService;
import teamnova.elite_gear.service.OrderService;
import teamnova.elite_gear.service.ProductService;
import java.util.Collections;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OrderItemResourceTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private OrderItemService orderItemService;

    @Mock
    private OrderService orderService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderItemResource orderItemResource;

    private OrderItemDTO orderItemDTO;
    private UUID orderItemId;

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
                .standaloneSetup(orderItemResource)
                .setValidator(noOpValidator)
                .build();

        objectMapper = new ObjectMapper();
        orderItemId = UUID.randomUUID();
        orderItemDTO = new OrderItemDTO();
        orderItemDTO.setOrderItemID(orderItemId);
        orderItemDTO.setQuantity(2);
        orderItemDTO.setUnitPrice(100);
        orderItemDTO.setTotalPrice(200);
        orderItemDTO.setProductVariantId(UUID.randomUUID());
    }

    @Test
    void testGetAllOrderItems() throws Exception {
        when(orderItemService.findAll()).thenReturn(Collections.singletonList(orderItemDTO));

        mockMvc.perform(get("/api/orderItems"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].orderItemID").value(orderItemId.toString()))
                .andExpect(jsonPath("$[0].quantity").value(2))
                .andExpect(jsonPath("$[0].unitPrice").value(100))
                .andExpect(jsonPath("$[0].totalPrice").value(200));
    }

    @Test
    void testGetOrderItem() throws Exception {
        when(orderItemService.get(orderItemId)).thenReturn(orderItemDTO);

        mockMvc.perform(get("/api/orderItems/{orderItemID}", orderItemId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderItemID").value(orderItemId.toString()))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.unitPrice").value(100))
                .andExpect(jsonPath("$.totalPrice").value(200));
    }

    @Test
    void testUpdateOrderItem() throws Exception {
        mockMvc.perform(put("/api/orderItems/{orderItemID}", orderItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderItemDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(orderItemId.toString()));

        verify(orderItemService).update(eq(orderItemId), any(OrderItemDTO.class));
    }

    @Test
    void testDeleteOrderItem() throws Exception {
        mockMvc.perform(delete("/api/orderItems/{orderItemID}", orderItemId))
                .andExpect(status().isNoContent());

        verify(orderItemService).delete(orderItemId);
    }
}
