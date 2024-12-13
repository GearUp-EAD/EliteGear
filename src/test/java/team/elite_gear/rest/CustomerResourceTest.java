package team.elite_gear.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import teamnova.elite_gear.model.CustomerDTO;
import teamnova.elite_gear.rest.CustomerResource;
import teamnova.elite_gear.service.CustomerService;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CustomerResourceTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerResource customerResource;

    private CustomerDTO customerDTO;
    private UUID customerId;

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
                .standaloneSetup(customerResource)
                .setValidator(noOpValidator)
                .build();

        objectMapper = new ObjectMapper();
        customerId = UUID.randomUUID();
        customerDTO = new CustomerDTO();
        customerDTO.setCustomerID(customerId);
        customerDTO.setName("Test Customer");
    }



    @Test
    void testGetAllCustomers() throws Exception {
        when(customerService.findAll()).thenReturn(Collections.singletonList(customerDTO));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].customerID").value(customerId.toString()))
                .andExpect(jsonPath("$[0].name").value("Test Customer"));
    }

    @Test
    void testGetCustomer() throws Exception {
        when(customerService.get(customerId)).thenReturn(customerDTO);

        mockMvc.perform(get("/api/customers/{customerID}", customerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.customerID").value(customerId.toString()))
                .andExpect(jsonPath("$.name").value("Test Customer"));
    }

    @Test
    void testGetFirstFiveCustomers() throws Exception {
        when(customerService.findFirstFiveCustomers()).thenReturn(Collections.singletonList(customerDTO));

        mockMvc.perform(get("/api/customers/first-five"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].customerID").value(customerId.toString()))
                .andExpect(jsonPath("$[0].name").value("Test Customer"));
    }

    @Test
    void testCreateCustomer() throws Exception {
        when(customerService.create(any(CustomerDTO.class))).thenReturn(customerId);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(customerId.toString()));
    }

    @Test
    void testUpdateCustomer() throws Exception {
        mockMvc.perform(put("/api/customers/{customerID}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(customerId.toString()));

        verify(customerService).update(eq(customerId), any(CustomerDTO.class));
    }

    @Test
    void testDeleteCustomer() throws Exception {
        mockMvc.perform(delete("/api/customers/{customerID}", customerId))
                .andExpect(status().isNoContent());

        verify(customerService).delete(customerId);
    }




}