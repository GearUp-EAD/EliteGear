package team.elite_gear;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import teamnova.elite_gear.domain.*;
import teamnova.elite_gear.model.*;
import teamnova.elite_gear.repos.*;
import teamnova.elite_gear.service.ProductService;
import teamnova.elite_gear.util.NotFoundException;

import java.util.*;

class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductVariantRepository variantRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SizeRepository sizeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProducts() {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        UUID sizeId = UUID.randomUUID();
        UUID variantId = UUID.randomUUID(); // Add a variant ID

        CreateProductRequest request = new CreateProductRequest();
        request.setName("Test Product");
        request.setDescription("Description");
        request.setBasePrice(100);
        request.setImageUrl("url");
        request.setCategoryId(categoryId);

        CreateVariantRequest variantRequest = new CreateVariantRequest();
        variantRequest.setSizeId(sizeId);
        variantRequest.setStockQuantity(50);
        variantRequest.setPriceAdjustment(10);

        request.setVariants(List.of(variantRequest));

        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setCategoryName("Test Category");

        Size size = new Size();
        size.setSizeId(sizeId);
        size.setValue("M"); // Set the value for Size (e.g., "M" for Medium)

        ProductVariant variant = new ProductVariant(); // Create a ProductVariant
        variant.setVariantId(variantId);
        variant.setStockQuantity(50);
        variant.setPriceAdjustment(10);
        variant.setSize(size); // Assign the size to the variant

        Product savedProduct = new Product();
        savedProduct.setProductID(UUID.randomUUID());
        savedProduct.setCategory(category);
        savedProduct.setVariants(Set.of(variant)); // Assign the variant to the product

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(sizeRepository.findById(sizeId)).thenReturn(Optional.of(size));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(variantRepository.save(any(ProductVariant.class))).thenReturn(variant); // Mock saving the variant

        // Act
        List<ProductDTO> createdProducts = productService.createProducts(List.of(request));

        // Assert
        assertNotNull(createdProducts);
        assertEquals(1, createdProducts.size());
        assertEquals("Test Category", createdProducts.get(0).getCategoryName());
        assertNotNull(createdProducts.get(0).getVariants());
        assertEquals(1, createdProducts.get(0).getVariants().size()); // Verify variant is present
        assertEquals("M", createdProducts.get(0).getVariants().get(0).getSize()); // Verify size is correctly mapped

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(sizeRepository, times(1)).findById(sizeId);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(variantRepository, times(1)).save(any(ProductVariant.class));
    }



    @Test
    void testFindProductsByCategoryId() {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setCategoryName("Test Category");

        Product product = new Product();
        product.setProductID(UUID.randomUUID());
        product.setName("Test Product");
        product.setCategory(category); // Set the category for the product

        // Mock the repository to return the list with the product when querying by categoryId
        when(productRepository.findByCategoryCategoryId(categoryId)).thenReturn(List.of(product));

        // Act
        List<ProductDTO> products = productService.findProducts(categoryId, null);

        // Assert
        assertNotNull(products);
        assertEquals(1, products.size());  // Expect 1 product
        assertEquals("Test Product", products.get(0).getName());
        assertEquals("Test Category", products.get(0).getCategoryName());  // Ensure category name matches
    }


    @Test
    void testGetProduct() {
        // Arrange
        UUID productId = UUID.randomUUID();

        // Create and set up the Category object
        Category category = new Category();
        category.setCategoryId(UUID.randomUUID());
        category.setCategoryName("Test Category"); // Set the category name

        // Create the Product object and assign the Category
        Product product = new Product();
        product.setProductID(productId);
        product.setName("Test Product");
        product.setCategory(category); // Ensure the category is set

        // Mock the productRepository to return the product with the category
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act
        ProductDTO productDTO = productService.getProduct(productId);

        // Assert
        assertNotNull(productDTO);
        assertEquals("Test Product", productDTO.getName());
        assertNotNull(productDTO.getCategoryName()); // Ensure category name is not null
        assertEquals("Test Category", productDTO.getCategoryName()); // Check the category name
        verify(productRepository, times(1)).findById(productId);
    }


    @Test
    void testDeleteProduct() {
        // Arrange
        UUID productId = UUID.randomUUID();
        Product product = new Product();
        product.setProductID(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act
        productService.deleteProduct(productId);

        // Assert
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void testGetProductAvailability() {
        // Arrange
        UUID productId = UUID.randomUUID();

        // Create a Size object and set its value
        Size size = new Size();
        size.setSizeId(UUID.randomUUID());
        size.setValue("Large"); // Ensure a valid value is set

        // Create a ProductVariant and assign the Size object
        ProductVariant variant = new ProductVariant();
        variant.setVariantId(UUID.randomUUID());
        variant.setStockQuantity(10);
        variant.setSize(size); // Assign the Size object

        // Mock the variantRepository to return the variant with the size object
        when(variantRepository.findByProductProductID(productId)).thenReturn(List.of(variant));

        // Act
        List<ProductVariantDTO> availability = productService.getProductAvailability(productId);

        // Assert
        assertNotNull(availability);
        assertEquals(1, availability.size());
        assertEquals(10, availability.get(0).getStockQuantity());
        assertNotNull(availability.get(0).getSize()); // Ensure size is not null
    }


    @Test
    void testUpdateVariants() {
        // Arrange
        UUID variantId = UUID.randomUUID();
        ProductVariant existingVariant = new ProductVariant();
        existingVariant.setVariantId(variantId);
        existingVariant.setStockQuantity(5);

        ProductVariantDTO variantDTO = new ProductVariantDTO();
        variantDTO.setVariantId(variantId);
        variantDTO.setStockQuantity(10);

        when(variantRepository.findById(variantId)).thenReturn(Optional.of(existingVariant));

        // Act
        List<ProductVariantDTO> updatedVariants = productService.updateVariats(List.of(variantDTO));

        // Assert
        assertNotNull(updatedVariants);
        assertEquals(10, updatedVariants.get(0).getStockQuantity());
        verify(variantRepository, times(1)).save(existingVariant);
    }
}
