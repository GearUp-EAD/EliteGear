package teamnova.elite_gear.service;

import java.util.*;
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

    public List<ProductDTO> createProducts(List<CreateProductRequest> requests) {
        List<ProductDTO> createdProducts = new ArrayList<>();

        for (CreateProductRequest request : requests) {
            // 1. Create the product
            Product product = new Product();
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setBasePrice(request.getBasePrice());
            product.setImageUrl(request.getImageUrl());

            // 2. Set category
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            product.setCategory(category);

            // 3. Save the product
            product = productRepository.save(product);

            // 4. Create and save variants
            Set<ProductVariant> variants = new HashSet<>();
            for (CreateVariantRequest variantRequest : request.getVariants()) {
                ProductVariant variant = new ProductVariant();
                variant.setProduct(product);  // Set the product reference

                // Set size
                Size size = sizeRepository.findById(variantRequest.getSizeId())
                        .orElseThrow(() -> new NotFoundException("Size not found"));
                variant.setSize(size);

                // Set variant-specific attributes
                variant.setStockQuantity(variantRequest.getStockQuantity());
                variant.setPriceAdjustment(variantRequest.getPriceAdjustment());

                // Save variant
                variant = variantRepository.save(variant);
                variants.add(variant);
            }

            // 5. Update the product with variants and save again
            product.setVariants(variants);

            // 6. Convert to DTO and add to the response list
            createdProducts.add(mapToDTO(product));
        }

        return createdProducts;
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

    @Transactional
    public void deleteProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));

        productRepository.delete(product);
    }




    public List<ProductVariantDTO> getProductAvailability(UUID productId) {
        return variantRepository.findByProductProductID(productId).stream()
                .map(this::mapVariantToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductVariantDTO> updateVariats (List<ProductVariantDTO> variants) {
        for (ProductVariantDTO variant : variants) {
            ProductVariant productVariant = variantRepository.findById(variant.getVariantId())
                    .orElseThrow(() -> new NotFoundException("Variant not found"));
            productVariant.setStockQuantity(variant.getStockQuantity());
            productVariant.setPriceAdjustment(variant.getPriceAdjustment());
            variantRepository.save(productVariant);

    }
        return variants;
    }

    public ProductDTO mapToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductID());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setBasePrice(product.getBasePrice());
        dto.setCategoryName(product.getCategory().getCategoryName());
        dto.setImageUrl(product.getImageUrl());
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
