package team.elite_gear;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import teamnova.elite_gear.domain.Product;
import teamnova.elite_gear.model.ProductDTO;
import teamnova.elite_gear.rest.ProductResource;
import teamnova.elite_gear.service.ProductService;
import teamnova.elite_gear.util.NotFoundException;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProductResourceTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductResource productResource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productResource).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllProduct_ShouldReturnAllProduct_WhenSuccess() throws Exception {
        List<ProductDTO> productList = List.of(new ProductDTO(), new ProductDTO());
        UUID uuid = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
        String sizeType = "sizeType";
        when(productService.findProducts(uuid, sizeType)).thenReturn(productList);

        mockMvc.perform(get("/api/products")
                .param("categoryId", uuid.toString())
                .param("sizeType", sizeType))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(productList)));

        verify(productService).findProducts(uuid, sizeType);
    }

    @Test
    void getAllProducts_ShouldReturnOk_WhenListIsEmpty() throws Exception {
        List<ProductDTO> productList = List.of();
        UUID uuid = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
        String sizeType = "sizeType";
        when(productService.findProducts(uuid, sizeType)).thenReturn(productList);

        mockMvc.perform(get("/api/products")
                .param("categoryId", uuid.toString())
                .param("sizeType", sizeType))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(productList)));

        verify(productService).findProducts(uuid, sizeType);
    }

    @Test
    void getProduct_ShouldReturnProduct_WhenSuccess() throws Exception {
        ProductDTO productDTO = new ProductDTO();
        UUID uuid = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
        when(productService.getProduct(uuid)).thenReturn(productDTO);

        mockMvc.perform(get("/api/products/" + uuid))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(productDTO)));

        verify(productService).getProduct(uuid);
    }

    @Test
    void getProduct_ShouldReturnNotFound_WHenHaveEmptyList() throws Exception {
        UUID uuid = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
        when(productService.getProduct(uuid)).thenThrow(new NotFoundException("Product not found"));

        mockMvc.perform(get("/api/products/" + uuid))
                .andExpect(status().isNotFound());

        verify(productService).getProduct(uuid);
    }
}
