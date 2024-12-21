package team.elite_gear.rest;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import teamnova.elite_gear.model.ShippingDTO;
import teamnova.elite_gear.model.ShippingUpdateDTO;
import teamnova.elite_gear.rest.ShippingResource;
import teamnova.elite_gear.service.ShippingService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ShippingResourceTest {

    @Mock
    private ShippingService shippingService;

    @InjectMocks
    private ShippingResource shippingResource;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(shippingResource).build();
        objectMapper = new ObjectMapper();

    }

    @Test
    void testGetAllShippings() throws Exception {
        ShippingDTO shippingDTO = new ShippingDTO();
        shippingDTO.setShippingID(UUID.randomUUID());
        shippingDTO.setShippingDate(LocalDate.now());
        shippingDTO.setShippingAddress("123 Test St.");
        shippingDTO.setShippingStatus("Delivered");

        when(shippingService.findAll()).thenReturn(Collections.singletonList(shippingDTO));

        mockMvc.perform(get("/api/shippings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].shippingID").value(shippingDTO.getShippingID().toString()));

        verify(shippingService, times(1)).findAll();
    }

    @Test
    void testGetShipping() throws Exception {
        UUID shippingID = UUID.randomUUID();
        ShippingDTO shippingDTO = new ShippingDTO();
        shippingDTO.setShippingID(shippingID);
        shippingDTO.setShippingDate(LocalDate.now());
        shippingDTO.setShippingAddress("123 Test St.");
        shippingDTO.setShippingStatus("Delivered");

        when(shippingService.get(shippingID)).thenReturn(shippingDTO);

        mockMvc.perform(get("/api/shippings/{shippingID}", shippingID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shippingID").value(shippingID.toString()));

        verify(shippingService, times(1)).get(shippingID);
    }

    @Test
    void testGetShippingByOrderID() throws Exception {
        UUID orderID = UUID.randomUUID();
        ShippingDTO shippingDTO = new ShippingDTO();
        shippingDTO.setOrder(orderID);

        when(shippingService.getByOrderID(orderID)).thenReturn(shippingDTO);

        mockMvc.perform(get("/api/shippings/order/{orderID}", orderID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.order").value(orderID.toString()));

        verify(shippingService, times(1)).getByOrderID(orderID);
    }

//    @Test
//    void testCreateShipping() throws Exception {
//        ShippingDTO shippingDTO = new ShippingDTO();
//        UUID shippingID = UUID.randomUUID();
//
//        when(shippingService.create(any(ShippingDTO.class))).thenReturn(shippingID);
//
//        mockMvc.perform(post("/api/shippings")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(new ShippingDTO())))
//                .andExpect(status().isCreated());
//
//        verify(shippingService, times(1)).create(any(ShippingDTO.class));
//    }
//
//    @Test
//    void testUpdateShipping() throws Exception {
//        UUID shippingID = UUID.randomUUID();
//        ShippingDTO shippingDTO = new ShippingDTO();
//
//        doNothing().when(shippingService).update(eq(shippingID), any(ShippingDTO.class));
//
//        mockMvc.perform(put("/api/shippings/{shippingID}", shippingID)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(shippingDTO)))
//                .andExpect(status().isOk());
//
//        verify(shippingService, times(1)).update(eq(shippingID), any(ShippingDTO.class));
//    }

    @Test
    void testUpdateShippingByOrderID() throws Exception {
        UUID orderID = UUID.randomUUID();
        ShippingUpdateDTO shippingUpdateDTO = new ShippingUpdateDTO();
        shippingUpdateDTO.setShippingStatus("Shipped");

        doNothing().when(shippingService).updateShippingStatus(orderID, shippingUpdateDTO.getShippingStatus());

        mockMvc.perform(put("/api/shippings/order/{orderID}", orderID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shippingUpdateDTO)))
                .andExpect(status().isOk());

        verify(shippingService, times(1)).updateShippingStatus(orderID, shippingUpdateDTO.getShippingStatus());
    }

    @Test
    void testDeleteShipping() throws Exception {
        UUID shippingID = UUID.randomUUID();

        doNothing().when(shippingService).delete(shippingID);

        mockMvc.perform(delete("/api/shippings/{shippingID}", shippingID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(shippingService, times(1)).delete(shippingID);
    }
}
