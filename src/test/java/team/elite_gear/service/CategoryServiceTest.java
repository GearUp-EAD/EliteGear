package team.elite_gear.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import teamnova.elite_gear.domain.Category;
import teamnova.elite_gear.domain.Product;
import teamnova.elite_gear.model.CategoryDTO;
import teamnova.elite_gear.repos.CategoryRepository;
import teamnova.elite_gear.repos.ProductRepository;
import teamnova.elite_gear.service.CategoryService;
import teamnova.elite_gear.util.NotFoundException;
import teamnova.elite_gear.util.ReferencedWarning;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryService categoryService;

    private UUID categoryId;
    private Category category;
    private CategoryDTO categoryDTO;
    private Product product;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();

        category = new Category();
        category.setCategoryId(categoryId);
        category.setCategoryName("Electronics");
        category.setImageUrl("http://example.com/electronics.jpg");

        categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryID(categoryId);
        categoryDTO.setCategoryName("Electronics");
        categoryDTO.setImageUrl("http://example.com/electronics.jpg");

        product = new Product();
        product.setProductID(UUID.randomUUID());
        product.setName("Laptop");
        product.setDescription("High-performance laptop");
        product.setBasePrice(1000);
        product.setCategory(category);
    }

    @Test
    void testFindAll() {
        when(categoryRepository.findAll(Sort.by("categoryName"))).thenReturn(List.of(category));

        List<CategoryDTO> result = categoryService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getCategoryName());

        verify(categoryRepository, times(1)).findAll(Sort.by("categoryName"));
    }

    @Test
    void testGetCategory_Success() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        CategoryDTO result = categoryService.get(categoryId);

        assertNotNull(result);
        assertEquals(categoryId, result.getCategoryID());
        assertEquals("Electronics", result.getCategoryName());

        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void testGetCategory_NotFound() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.get(categoryId));

        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void testGetParentCategories() {
        when(categoryRepository.findByParentCategoryIsNull()).thenReturn(List.of(category));

        List<CategoryDTO> result = categoryService.getParentCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getCategoryName());

        verify(categoryRepository, times(1)).findByParentCategoryIsNull();
    }

    @Test
    void testGetSubcategories() {
        Category subcategory = new Category();
        subcategory.setCategoryId(UUID.randomUUID());
        subcategory.setCategoryName("Laptops");
        subcategory.setParentCategory(category);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.findByParentCategory(category)).thenReturn(List.of(subcategory));

        List<CategoryDTO> result = categoryService.getSubcategories(categoryId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptops", result.get(0).getCategoryName());

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).findByParentCategory(category);
    }

    @Test
    void testCreateCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        UUID result = categoryService.create(categoryDTO);

        assertNotNull(result);
        assertEquals(categoryId, result);

        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testUpdateCategory() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        categoryService.update(categoryId, categoryDTO);

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void testDeleteCategory() {
        categoryService.delete(categoryId);

        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    void testGetReferencedWarning_Referenced() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(productRepository.findFirstByCategory(category)).thenReturn(product);

        ReferencedWarning warning = categoryService.getReferencedWarning(categoryId);

        assertNotNull(warning);
        assertEquals("category.product.category.referenced", warning.getKey());
        assertEquals(product.getProductID(), warning.getParams().get(0));

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(productRepository, times(1)).findFirstByCategory(category);
    }

    @Test
    void testGetReferencedWarning_NotReferenced() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(productRepository.findFirstByCategory(category)).thenReturn(null);

        ReferencedWarning warning = categoryService.getReferencedWarning(categoryId);

        assertNull(warning);

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(productRepository, times(1)).findFirstByCategory(category);
    }
}
