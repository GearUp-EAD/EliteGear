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

    @Query(value = """
       SELECT c.name AS customerName,
                                 c.image_url AS imageUrl,
                                 p.payment_date AS paymentDate,
                                 p.payment_amount AS paymentAmount,
                      		   p.payment_method AS PaymentMethod
                          FROM payments p
                          JOIN orders o ON p.order_id = o.orderid
                          JOIN customers c ON o.customer_id = c.customerid
                          ORDER BY p.payment_date DESC
                          LIMIT 5
""", nativeQuery = true)
    List<Object[]> findLatestFiveTransactionsNative();

    @Query(value = """
       WITH monthly_totals AS (
                                                           SELECT
                                                               DATE_TRUNC('month', payment_date) AS month,
                                                               SUM(payment_amount) AS total_payment_amount
                                                           FROM
                                                               payments
                                                           GROUP BY
                                                               DATE_TRUNC('month', payment_date)
                                                           ORDER BY
                                                               month
                                                       ),
                                                       growth_calculation AS (
                                                           SELECT
                                                               month,
                                                               total_payment_amount,
                                                               LAG(total_payment_amount) OVER (ORDER BY month) AS previous_month_payment
                                                           FROM
                                                               monthly_totals
                                                       )
                                                       SELECT
                                                           month,
                                                           CASE
                                                               WHEN previous_month_payment IS NULL THEN NULL
                                                               ELSE ROUND(((total_payment_amount - previous_month_payment) * 100.0) / previous_month_payment, 2)
                                                           END AS growth_percentage
                                                       FROM
                                                           growth_calculation;
                                                       
""", nativeQuery = true)
    List<Object[]> getMonthlyPaymentGrowth();
}
