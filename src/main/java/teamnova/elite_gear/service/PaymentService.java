package teamnova.elite_gear.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import teamnova.elite_gear.domain.Order;
import teamnova.elite_gear.domain.Payment;
import teamnova.elite_gear.model.PaymentDTO;
import teamnova.elite_gear.repos.OrderRepository;
import teamnova.elite_gear.repos.PaymentRepository;
import teamnova.elite_gear.util.NotFoundException;


@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(final PaymentRepository paymentRepository,
                          final OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;

    }

    public List<PaymentDTO> findAll() {
        final List<Payment> payments = paymentRepository.findAll(Sort.by("id"));
        return payments.stream()
                .map(payment -> mapToDTO(payment, new PaymentDTO()))
                .toList();
    }

    public PaymentDTO get(final UUID id) {
        return paymentRepository.findById(id)
                .map(payment -> mapToDTO(payment, new PaymentDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public UUID create(final PaymentDTO paymentDTO) {
        final Payment payment = new Payment();
        mapToEntity(paymentDTO, payment);
        return paymentRepository.save(payment).getId();
    }

    public void update(final UUID id, final PaymentDTO paymentDTO) {
        final Payment payment = paymentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(paymentDTO, payment);
        paymentRepository.save(payment);
    }

    public void delete(final UUID id) {
        paymentRepository.deleteById(id);
    }





    private PaymentDTO mapToDTO(final Payment payment, final PaymentDTO paymentDTO) {
        paymentDTO.setId(payment.getId());
        paymentDTO.setPaymentDate(payment.getPaymentDate());
        paymentDTO.setPaymentAmount(payment.getPaymentAmount());
        paymentDTO.setPaymentMethod(payment.getPaymentMethod());
        paymentDTO.setOrder(payment.getOrder() == null ? null : payment.getOrder().getOrderID());
        return paymentDTO;
    }

    private Payment mapToEntity(final PaymentDTO paymentDTO, final Payment payment) {
        payment.setPaymentDate(paymentDTO.getPaymentDate());
        payment.setPaymentAmount(paymentDTO.getPaymentAmount());
        payment.setPaymentMethod(paymentDTO.getPaymentMethod());

        final Order order = paymentDTO.getOrder() == null ? null : orderRepository.findById(paymentDTO.getOrder())
                .orElseThrow(() -> new NotFoundException("order not found"));
        payment.setOrder(order);
        return payment;
    }

    public boolean orderExists(final UUID orderID) {
        return paymentRepository.existsByOrderOrderID(orderID);
    }

    public String generateHash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(input.getBytes());
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
