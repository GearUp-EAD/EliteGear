package teamnova.elite_gear.service;

import java.util.List;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import teamnova.elite_gear.domain.Customer;
import teamnova.elite_gear.domain.Order;
import teamnova.elite_gear.model.CustomerDTO;
import teamnova.elite_gear.repos.CustomerRepository;
import teamnova.elite_gear.repos.OrderRepository;
import teamnova.elite_gear.util.NotFoundException;
import teamnova.elite_gear.util.ReferencedWarning;


@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public CustomerService(final CustomerRepository customerRepository,
            final OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    public List<CustomerDTO> findAll() {
        final List<Customer> customers = customerRepository.findAll(Sort.by("customerID"));
        return customers.stream()
                .map(customer -> mapToDTO(customer, new CustomerDTO()))
                .toList();
    }

    public CustomerDTO get(final UUID customerID) {
        return customerRepository.findById(customerID)
                .map(customer -> mapToDTO(customer, new CustomerDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public UUID create(final CustomerDTO customerDTO) {
        final Customer customer = new Customer();
        mapToEntity(customerDTO, customer);
        return customerRepository.save(customer).getCustomerID();
    }

    public void update(final UUID customerID, final CustomerDTO customerDTO) {
        final Customer customer = customerRepository.findById(customerID)
                .orElseThrow(NotFoundException::new);
        mapToEntity(customerDTO, customer);
        customerRepository.save(customer);
    }

    public void delete(final UUID customerID) {
        customerRepository.deleteById(customerID);
    }

    public UUID checkCustomer(final JSONObject jsonObject) {
        if (!customerRepository.existsByEmailIgnoreCase(jsonObject.getString("email"))) {
            Customer customer = new Customer();
            jsonObjectMapToEntity(jsonObject, customer);
            return customerRepository.save(customer).getCustomerID();
        } else {
            return customerRepository.findByEmailIgnoreCase(jsonObject.getString("email")).get().getCustomerID();
        }
    }

    private CustomerDTO mapToDTO(final Customer customer, final CustomerDTO customerDTO) {
        customerDTO.setCustomerID(customer.getCustomerID());
        customerDTO.setName(customer.getName());
        customerDTO.setEmail(customer.getEmail());
        customerDTO.setAddress(customer.getAddress());
        customerDTO.setPhoneNumber(customer.getPhoneNumber());
        customerDTO.setImageUrl(customer.getImageUrl());
        return customerDTO;
    }

    private Customer jsonObjectMapToEntity (JSONObject jsonObject, final Customer customer) {
        customer.setName(jsonObject.getString("name"));
        customer.setEmail(jsonObject.getString("email"));
        return customer;
    }

    private Customer mapToEntity(final CustomerDTO customerDTO, final Customer customer) {
        customer.setName(customerDTO.getName());
        customer.setEmail(customerDTO.getEmail());
        customer.setAddress(customerDTO.getAddress());
        customer.setPhoneNumber(customerDTO.getPhoneNumber());
        customer.setImageUrl(customerDTO.getImageUrl());
        return customer;
    }

    public boolean emailExists(final String email) {
        return customerRepository.existsByEmailIgnoreCase(email);
    }

    public boolean phoneNumberExists(final Integer phoneNumber) {
        return customerRepository.existsByPhoneNumber(phoneNumber);
    }

    public ReferencedWarning getReferencedWarning(final UUID customerID) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Customer customer = customerRepository.findById(customerID)
                .orElseThrow(NotFoundException::new);
        final Order customerOrder = orderRepository.findFirstByCustomer(customer);
        if (customerOrder != null) {
            referencedWarning.setKey("customer.order.customer.referenced");
            referencedWarning.addParam(customerOrder.getOrderID());
            return referencedWarning;
        }
        return null;
    }

}
