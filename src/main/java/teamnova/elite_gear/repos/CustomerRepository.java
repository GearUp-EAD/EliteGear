package teamnova.elite_gear.repos;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import teamnova.elite_gear.domain.Customer;
import java.util.List;


public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhoneNumber(Integer phoneNumber);

    Optional<Customer> findByEmailIgnoreCase(String email);

    @Query("SELECT c FROM Customer c ORDER BY c.createdAt ASC")
    List<Customer> findFirstFiveCustomers(Pageable pageable);

    default List<Customer> findFirstFiveCustomers() {
        return findFirstFiveCustomers(PageRequest.of(0, 5));
    }
}
