package team.elite_gear.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import teamnova.elite_gear.model.*;
import teamnova.elite_gear.rest.PaymentResource;
import teamnova.elite_gear.service.PaymentService;
import teamnova.elite_gear.util.NotFoundException;
import org.mockito.ArgumentCaptor;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PaymentResourceTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentResource paymentResource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentResource)
                .setValidator(mock(Validator.class)) // Add a mock Validator here
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register the JavaTimeModule

        PaymentDTO paymentDTO = new PaymentDTO();
    }

    @Test
    void getAllPayments_ShouldReturnAllPayments_WhenSuccess() throws Exception {
        List<PaymentDTO> paymentList = List.of(new PaymentDTO(), new PaymentDTO());
        when(paymentService.findAll()).thenReturn(paymentList);

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(paymentList)));

        verify(paymentService).findAll();
    }

    @Test
    void getPayment_ShouldReturnPayment_WhenSuccess() throws Exception {
        PaymentDTO paymentDTO = new PaymentDTO();
        UUID uuid = UUID.randomUUID();
        when(paymentService.get(uuid)).thenReturn(paymentDTO);

        mockMvc.perform(get("/api/payments/" + uuid))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(paymentDTO)));

        verify(paymentService).get(uuid);
    }

    @Test
    void getPayment_ShouldReturnNotFound_WhenPaymentNotFound() throws Exception {
        UUID uuid = UUID.randomUUID();
        when(paymentService.get(uuid)).thenThrow(new NotFoundException("Payment not found"));

        mockMvc.perform(get("/api/payments/" + uuid))
                .andExpect(status().isNotFound());

        verify(paymentService).get(uuid);
    }



    @Test
    void getTotalPaymentAmount_ShouldReturnTotalAmount_WhenSuccess() throws Exception {
        int totalAmount = 1000;
        when(paymentService.getTotalPaymentAmount()).thenReturn(totalAmount);

        mockMvc.perform(get("/api/payments/total"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(totalAmount)));

        verify(paymentService).getTotalPaymentAmount();
    }

    @Test
    void getPaymentSummary_ShouldReturnPaymentSummary_WhenSuccess() throws Exception {
        List<PaymentSummaryDTO> summaryList = List.of(new PaymentSummaryDTO(2023, 10, 5000));
        when(paymentService.getPaymentSummary()).thenReturn(summaryList);

        mockMvc.perform(get("/api/payments/paymentSummary"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(summaryList)));

        verify(paymentService).getPaymentSummary();
    }

    @Test
    void getLatestFiveTransactions_ShouldReturnTransactions_WhenSuccess() throws Exception {
        List<Object[]> transactions = List.of(new Object[]{"TXN1", 100}, new Object[]{"TXN2", 200});
        when(paymentService.getLatestFiveTransactions()).thenReturn(transactions);

        mockMvc.perform(get("/api/payments/latestFiveTransactions"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(transactions)));

        verify(paymentService).getLatestFiveTransactions();
    }

    @Test
    void getMonthlyPaymentGrowth_ShouldReturnGrowth_WhenSuccess() throws Exception {
        List<Object[]> growth = List.of(new Object[]{"January", 500}, new Object[]{"February", 700});
        when(paymentService.getMonthlyPaymentGrowth()).thenReturn(growth);

        mockMvc.perform(get("/api/payments/MonthlyPaymentGrowth"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(growth)));

        verify(paymentService).getMonthlyPaymentGrowth();
    }

    @Test
    void createPayment_ShouldReturnCreatedId_WhenSuccess() throws Exception {
        PaymentDTO paymentDTO = new PaymentDTO();
        UUID createdId = UUID.randomUUID();
        paymentDTO.setPaymentMethod("CREDIT_CARD");
        paymentDTO.setPaymentDate(LocalDate.now());
        paymentDTO.setPaymentAmount(1000);
        when(paymentService.create(paymentDTO)).thenReturn(createdId);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string(createdId.toString()));

        verify(paymentService).create(paymentDTO);
    }

    @Test
    void startPayment_ShouldReturnPaymentResponse_WhenSuccess() throws Exception {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOrder_id("order123");
        paymentRequest.setAmount("1000");
        paymentRequest.setCurrency("USD");

        PaymentResponse paymentResponse = new PaymentResponse("merchant123", "hash123");

        when(paymentService.startPayment(any(PaymentRequest.class))).thenReturn(paymentResponse);

        mockMvc.perform(post("/api/payments/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(paymentResponse)));

        verify(paymentService).startPayment(any(PaymentRequest.class));
    }

    @Test
    void paymentNotification_ShouldReturnPaymentId_WhenSuccess() throws Exception {
        PaymentNotification notification = new PaymentNotification();
        notification.setMerchantId("merchant123");
        notification.setOrder_id("order123");
        notification.setPayhere_amount("1000");
        notification.setPayhere_currency("USD");
        notification.setStatus_code("2");
        notification.setMd5sig("md5signature");

        UUID paymentId = UUID.randomUUID();
        when(paymentService.processPaymentNotification(any(PaymentNotification.class))).thenReturn(paymentId);

        mockMvc.perform(post("/api/payments/notify")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("merchantId", notification.getMerchantId())
                        .param("order_id", notification.getOrder_id())
                        .param("payhere_amount", notification.getPayhere_amount())
                        .param("payhere_currency", notification.getPayhere_currency())
                        .param("status_code", notification.getStatus_code())
                        .param("md5sig", notification.getMd5sig()))
                .andExpect(status().isOk())
                .andExpect(content().string("\"" + paymentId.toString() + "\""));

        verify(paymentService).processPaymentNotification(any(PaymentNotification.class));
    }


    @Test
    void updatePayment_ShouldReturnUpdatedId_WhenSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setPaymentMethod("CREDIT_CARD");
        paymentDTO.setPaymentDate(LocalDate.now());
        paymentDTO.setPaymentAmount(1000);

        doNothing().when(paymentService).update(any(UUID.class), any(PaymentDTO.class));

        mockMvc.perform(put("/api/payments/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("\"" + id.toString() + "\""));

        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<PaymentDTO> paymentDTOCaptor = ArgumentCaptor.forClass(PaymentDTO.class);
        verify(paymentService).update(idCaptor.capture(), paymentDTOCaptor.capture());

        assertEquals(id, idCaptor.getValue());
        assertEquals(paymentDTO.getPaymentMethod(), paymentDTOCaptor.getValue().getPaymentMethod());
        assertEquals(paymentDTO.getPaymentDate(), paymentDTOCaptor.getValue().getPaymentDate());
        assertEquals(paymentDTO.getPaymentAmount(), paymentDTOCaptor.getValue().getPaymentAmount());
    }

    @Test
    void deletePayment_ShouldReturnNoContent_WhenSuccess() throws Exception {
        UUID uuid = UUID.randomUUID();

        mockMvc.perform(delete("/api/payments/" + uuid))
                .andExpect(status().isNoContent());

        verify(paymentService).delete(uuid);
    }




}