package team.elite_gear.rest;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import teamnova.elite_gear.model.CategoryDTO;
import teamnova.elite_gear.rest.CategoryResource;
import teamnova.elite_gear.service.CategoryService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CategoryResourceTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryResource categoryResource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(categoryResource).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetAllCategories() throws Exception {
        List<CategoryDTO> categoryList = List.of(new CategoryDTO(), new CategoryDTO());
        when(categoryService.findAll()).thenReturn(categoryList);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(categoryList)));

        verify(categoryService).findAll();
    }

    @Test
    void testGetParentCategories() throws Exception {
        List<CategoryDTO> categoryList = List.of(new CategoryDTO(), new CategoryDTO());
        when(categoryService.getParentCategories()).thenReturn(categoryList);

        mockMvc.perform(get("/api/categories/parents"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(categoryList)));

        verify(categoryService).getParentCategories();
    }

    @Test
    void testGetSubcategories() throws Exception {
        List<CategoryDTO> categoryList = List.of(new CategoryDTO(), new CategoryDTO());
        UUID uuid = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
        when(categoryService.getSubcategories(uuid)).thenReturn(categoryList);

        mockMvc.perform(get("/api/categories/" + uuid + "/subcategories"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(categoryList)));

        verify(categoryService).getSubcategories(uuid);
    }

    @Test
    void testCreateCategory() throws Exception {
        UUID newCategoryId = UUID.randomUUID();
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryName("New Category");

        Mockito.when(categoryService.create(any(CategoryDTO.class))).thenReturn(newCategoryId);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(newCategoryId.toString()));
    }

    @Test
    void testUpdateCategory() throws Exception {
        UUID categoryId = UUID.randomUUID();
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryName("Updated Category");

        mockMvc.perform(put("/api/categories/{orderItemID}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(categoryId.toString()));

        ArgumentCaptor<CategoryDTO> categoryCaptor = ArgumentCaptor.forClass(CategoryDTO.class);
        verify(categoryService).update(eq(categoryId), categoryCaptor.capture());
        assertEquals("Updated Category", categoryCaptor.getValue().getCategoryName());
    }

    @Test
    void testDeleteCategory() throws Exception {
        UUID categoryId = UUID.randomUUID();

        mockMvc.perform(delete("/api/categories/{orderItemID}", categoryId))
                .andExpect(status().isNoContent());

        verify(categoryService).delete(categoryId);
    }


}
