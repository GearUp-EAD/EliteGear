package teamnova.elite_gear.repos;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import teamnova.elite_gear.domain.Customer;


public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhoneNumber(Integer phoneNumber);

    Optional<Customer> findByEmailIgnoreCase(String email);


}
