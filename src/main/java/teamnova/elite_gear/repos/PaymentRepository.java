package teamnova.elite_gear.repos;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import teamnova.elite_gear.domain.Order;
import teamnova.elite_gear.domain.Payment;
import teamnova.elite_gear.model.PaymentSummaryDTO;


public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Payment findFirstByOrder(Order order);

    boolean existsByOrderOrderID(UUID orderID);

    @Query("SELECT SUM(p.paymentAmount) FROM Payment p")
    Integer getTotalPaymentAmount();

    @Query("SELECT new teamnova.elite_gear.model.PaymentSummaryDTO(YEAR(p.paymentDate), MONTH(p.paymentDate), SUM(p.paymentAmount)) " +
            "FROM Payment p " +
            "GROUP BY YEAR(p.paymentDate), MONTH(p.paymentDate)")
    List<PaymentSummaryDTO> getMonthlyPaymentSummary();

}
