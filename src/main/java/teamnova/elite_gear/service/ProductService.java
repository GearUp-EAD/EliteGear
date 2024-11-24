package teamnova.elite_gear.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.stripe.model.InvoiceItem;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import teamnova.elite_gear.domain.*;
import teamnova.elite_gear.model.ProductDTO;
import teamnova.elite_gear.model.ProductVariantDTO;
import teamnova.elite_gear.repos.*;
import teamnova.elite_gear.util.NotFoundException;
import teamnova.elite_gear.util.ReferencedWarning;


@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final CategoryRepository categoryRepository;
    private final SizeRepository sizeRepository;

    public ProductDTO createProduct(CreateProductRequest request) {
        // 1. Create the product
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBasePrice(request.getBasePrice());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));
        product.setCategory(category);

        // 2. Save the product first
        product = productRepository.save(product);

        // 3. Create and save variants
        Set<ProductVariant> variants = new HashSet<>();
        for (CreateVariantRequest variantRequest : request.getVariants()) {
            ProductVariant variant = new ProductVariant();
            variant.setProduct(product);  // Set the product reference

            Size size = sizeRepository.findById(variantRequest.getSizeId())
                    .orElseThrow(() -> new NotFoundException("Size not found"));
            variant.setSize(size);

            variant.setStockQuantity(variantRequest.getStockQuantity());
            variant.setPriceAdjustment(variantRequest.getPriceAdjustment());

            variants.add(variant);  // Add to the set
            variantRepository.save(variant);
        }

        product.setVariants(variants);  // Set the variants in the product

        // 4. Return the created product
        return mapToDTO(productRepository.findById(product.getProductID()).get());
    }

    public List<ProductDTO> findProducts(UUID categoryId, String sizeType) {
        List<Product> products;
        if (categoryId != null) {
            products = productRepository.findByCategoryCategoryId(categoryId);
        } else if (sizeType != null) {
            products = productRepository.findByVariantsSizeSizeTypeName(sizeType);
        } else {
            products = productRepository.findAll();
        }
        return products.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO getProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        return mapToDTO(product);
    }



    public List<ProductVariantDTO> getProductAvailability(UUID productId) {
        return variantRepository.findByProductProductID(productId).stream()
                .map(this::mapVariantToDTO)
                .collect(Collectors.toList());
    }
    public ProductDTO mapToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductID());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setBasePrice(product.getBasePrice());
        dto.setCategoryName(product.getCategory().getCategoryName());
        dto.setVariants(product.getVariants().stream()
                .map(this::mapVariantToDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    public ProductVariantDTO mapVariantToDTO(ProductVariant variant) {
        ProductVariantDTO dto = new ProductVariantDTO();
        dto.setVariantId(variant.getVariantId());
        dto.setSize(variant.getSize().getValue());
        dto.setStockQuantity(variant.getStockQuantity());
        dto.setPriceAdjustment(variant.getPriceAdjustment());
        return dto;
    }



}
