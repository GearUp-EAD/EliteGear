package team.elite_gear.service;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import teamnova.elite_gear.domain.Customer;
import teamnova.elite_gear.domain.Order;
import teamnova.elite_gear.model.CustomerDTO;
import teamnova.elite_gear.repos.CustomerRepository;
import teamnova.elite_gear.repos.OrderRepository;
import teamnova.elite_gear.service.CustomerService;
import teamnova.elite_gear.util.ReferencedWarning;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setCustomerID(UUID.randomUUID());
        customer.setName("John Doe");
        customer.setEmail("johndoe@example.com");
        customer.setAddress("123 Main St");
        customer.setPhoneNumber(1234567890);
        customer.setCreatedAt(LocalDateTime.now());
        customer.setImageUrl("http://example.com/image.jpg");

        customerDTO = new CustomerDTO();
        customerDTO.setCustomerID(customer.getCustomerID());
        customerDTO.setName(customer.getName());
        customerDTO.setEmail(customer.getEmail());
        customerDTO.setAddress(customer.getAddress());
        customerDTO.setPhoneNumber(customer.getPhoneNumber());
        customerDTO.setCreatedAt(customer.getCreatedAt());
        customerDTO.setImageUrl(customer.getImageUrl());
    }

    @Test
    void testFindAll() {
        when(customerRepository.findAll(Sort.by("customerID"))).thenReturn(Collections.singletonList(customer));

        List<CustomerDTO> customers = customerService.findAll();

        assertNotNull(customers);
        assertEquals(1, customers.size());
        assertEquals(customer.getName(), customers.get(0).getName());
        verify(customerRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testGetCustomer() {
        when(customerRepository.findById(customer.getCustomerID())).thenReturn(Optional.of(customer));

        CustomerDTO result = customerService.get(customer.getCustomerID());

        assertNotNull(result);
        assertEquals(customer.getName(), result.getName());
        verify(customerRepository, times(1)).findById(customer.getCustomerID());
    }

    @Test
    void testCreateCustomer() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        UUID result = customerService.create(customerDTO);

        assertNotNull(result);
        assertEquals(customer.getCustomerID(), result);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testUpdateCustomer() {
        when(customerRepository.findById(customer.getCustomerID())).thenReturn(Optional.of(customer));

        customerService.update(customer.getCustomerID(), customerDTO);

        verify(customerRepository, times(1)).findById(customer.getCustomerID());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void testDeleteCustomer() {
        customerService.delete(customer.getCustomerID());

        verify(customerRepository, times(1)).deleteById(customer.getCustomerID());
    }

    @Test
    void testCheckCustomer_NewCustomer() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "Jane Doe");
        jsonObject.put("email", "janedoe@example.com");
        jsonObject.put("address", "456 Main St");
        jsonObject.put("phoneNumber", 987654321);

        when(customerRepository.existsByEmailIgnoreCase(jsonObject.getString("email"))).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        UUID result = customerService.checkCustomer(jsonObject);

        assertNotNull(result);
        verify(customerRepository, times(1)).existsByEmailIgnoreCase(jsonObject.getString("email"));
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testCheckCustomer_ExistingCustomer() throws JSONException{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", "johndoe@example.com");

        when(customerRepository.existsByEmailIgnoreCase(jsonObject.getString("email"))).thenReturn(true);
        when(customerRepository.findByEmailIgnoreCase(jsonObject.getString("email"))).thenReturn(Optional.of(customer));

        UUID result = customerService.checkCustomer(jsonObject);

        assertNotNull(result);
        assertEquals(customer.getCustomerID(), result);
        verify(customerRepository, times(1)).existsByEmailIgnoreCase(jsonObject.getString("email"));
        verify(customerRepository, times(1)).findByEmailIgnoreCase(jsonObject.getString("email"));
    }

    @Test
    void testFindFirstFiveCustomers() {
        when(customerRepository.findFirstFiveCustomers()).thenReturn(Collections.singletonList(customer));

        List<CustomerDTO> result = customerService.findFirstFiveCustomers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(customer.getName(), result.get(0).getName());
        verify(customerRepository, times(1)).findFirstFiveCustomers();
    }

    @Test
    void testReferencedWarning_NoOrders() {
        when(customerRepository.findById(customer.getCustomerID())).thenReturn(Optional.of(customer));
        when(orderRepository.findFirstByCustomer(customer)).thenReturn(null);

        ReferencedWarning warning = customerService.getReferencedWarning(customer.getCustomerID());

        assertNull(warning);
        verify(customerRepository, times(1)).findById(customer.getCustomerID());
        verify(orderRepository, times(1)).findFirstByCustomer(customer);
    }

    @Test
    void testReferencedWarning_WithOrders() {
        Order order = new Order();
        order.setOrderID(UUID.randomUUID());

        when(customerRepository.findById(customer.getCustomerID())).thenReturn(Optional.of(customer));
        when(orderRepository.findFirstByCustomer(customer)).thenReturn(order);

        ReferencedWarning warning = customerService.getReferencedWarning(customer.getCustomerID());

        assertNotNull(warning);
        assertEquals("customer.order.customer.referenced", warning.getKey());
        assertTrue(warning.getParams().contains(order.getOrderID()));
        verify(customerRepository, times(1)).findById(customer.getCustomerID());
        verify(orderRepository, times(1)).findFirstByCustomer(customer);
    }
}
