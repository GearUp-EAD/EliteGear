package team.elite_gear.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import teamnova.elite_gear.domain.Payment;
import teamnova.elite_gear.model.PaymentDTO;
import teamnova.elite_gear.repos.OrderRepository;
import teamnova.elite_gear.repos.PaymentRepository;
import teamnova.elite_gear.service.PaymentService;
import teamnova.elite_gear.util.NotFoundException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_ReturnsPaymentDTOList() {
        // Arrange
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentAmount(1000);
        payment.setPaymentMethod("Credit Card");

        when(paymentRepository.findAll(Sort.by("id"))).thenReturn(List.of(payment));

        // Act
        List<PaymentDTO> result = paymentService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(payment.getId(), result.get(0).getId());
        assertEquals(payment.getPaymentAmount(), result.get(0).getPaymentAmount());
        verify(paymentRepository, times(1)).findAll(Sort.by("id"));
    }

    @Test
    void findAll_ReturnsEmptyList() {
        // Arrange
        when(paymentRepository.findAll(Sort.by("id"))).thenReturn(Collections.emptyList());

        // Act
        List<PaymentDTO> result = paymentService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(paymentRepository, times(1)).findAll(Sort.by("id"));
    }

    @Test
    void get_ExistingId_ReturnsPaymentDTO() {
        // Arrange
        UUID id = UUID.randomUUID();
        Payment payment = new Payment();
        payment.setId(id);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentAmount(500);
        payment.setPaymentMethod("Debit Card");

        when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));

        // Act
        PaymentDTO result = paymentService.get(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(payment.getPaymentAmount(), result.getPaymentAmount());
        verify(paymentRepository, times(1)).findById(id);
    }

    @Test
    void get_NonExistingId_ThrowsNotFoundException() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(paymentRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> paymentService.get(id));
        verify(paymentRepository, times(1)).findById(id);
    }

    @Test
    void create_ValidPaymentDTO_ReturnsUUID() {
        // Arrange
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setPaymentDate(LocalDate.now());
        paymentDTO.setPaymentAmount(1200);
        paymentDTO.setPaymentMethod("PayPal");

        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // Act
        UUID result = paymentService.create(paymentDTO);

        // Assert
        assertNotNull(result);
        assertEquals(payment.getId(), result);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void delete_ExistingId_CallsRepositoryDelete() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        paymentService.delete(id);

        // Assert
        verify(paymentRepository, times(1)).deleteById(id);
    }

    @Test
    void delete_NonExistingId_CallsRepositoryDelete() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        paymentService.delete(id);

        // Assert
        verify(paymentRepository, times(1)).deleteById(id);
    }

    @Test
    void update_ValidPaymentDTO_UpdatesPayment() {
        // Arrange
        UUID id = UUID.randomUUID();
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setPaymentDate(LocalDate.now());
        paymentDTO.setPaymentAmount(800);
        paymentDTO.setPaymentMethod("Stripe");

        Payment existingPayment = new Payment();
        existingPayment.setId(id);

        when(paymentRepository.findById(id)).thenReturn(Optional.of(existingPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(existingPayment);

        // Act
        paymentService.update(id, paymentDTO);

        // Assert
        verify(paymentRepository, times(1)).findById(id);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void update_NonExistingId_ThrowsNotFoundException() {
        // Arrange
        UUID id = UUID.randomUUID();
        PaymentDTO paymentDTO = new PaymentDTO();

        when(paymentRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> paymentService.update(id, paymentDTO));
        verify(paymentRepository, times(1)).findById(id);
    }
}
