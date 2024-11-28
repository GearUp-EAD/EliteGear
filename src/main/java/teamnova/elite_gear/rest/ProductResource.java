package teamnova.elite_gear.rest;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamnova.elite_gear.domain.CreateProductRequest;
import teamnova.elite_gear.model.ProductDTO;
import teamnova.elite_gear.model.ProductVariantDTO;
import teamnova.elite_gear.service.ProductService;
import teamnova.elite_gear.util.ReferencedException;
import teamnova.elite_gear.util.ReferencedWarning;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductResource {
    private final ProductService productService;

    @GetMapping
    public List<ProductDTO> getAllProducts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String sizeType) {
        return productService.findProducts(categoryId, sizeType);
    }

    @GetMapping("/{productId}")
    public ProductDTO getProduct(@PathVariable UUID productId) {
        return productService.getProduct(productId);
    }
    @GetMapping("/{productId}/availability")
    public List<ProductVariantDTO> getProductAvailability(@PathVariable UUID productId) {
        return productService.getProductAvailability(productId);
    }

   @PutMapping("/{variantId}")
    public List <ProductVariantDTO> updateProductVariant(@PathVariable UUID variantId, @RequestBody List <ProductVariantDTO> variantDTO) {
        return productService.updateVariats( variantDTO);
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public List<ProductDTO> createProducts(@RequestBody List<CreateProductRequest> requests) {
        return productService.createProducts(requests);
    }


    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }


}

