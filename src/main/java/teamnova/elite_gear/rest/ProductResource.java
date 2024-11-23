package teamnova.elite_gear.rest;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamnova.elite_gear.model.ProductDTO;
import teamnova.elite_gear.service.ProductService;
import teamnova.elite_gear.util.ReferencedException;
import teamnova.elite_gear.util.ReferencedWarning;


@RestController
@RequestMapping(value = "/api/products", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductResource {

    private final ProductService productService;

    public ProductResource(final ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{productID}")
    public ResponseEntity<ProductDTO> getProduct(
            @PathVariable(name = "productID") final UUID productID) {
        return ResponseEntity.ok(productService.get(productID));
    }

    @GetMapping("/category/{categoryID}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(
            @PathVariable(name = "categoryID") final UUID categoryID) {
        return ResponseEntity.ok(productService.findByCategory(categoryID));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<List<UUID>> createProducts(@RequestBody @Valid final List<ProductDTO> productDTOs) {
        List<UUID> createdProductIDs = productDTOs.stream()
                .map(productService::create)
                .collect(Collectors.toList());
        return new ResponseEntity<>(createdProductIDs, HttpStatus.CREATED);
    }


    @PutMapping("/{productID}")
    public ResponseEntity<UUID> updateProduct(
            @PathVariable(name = "productID") final UUID productID,
            @RequestBody @Valid final ProductDTO productDTO) {
        productService.update(productID, productDTO);
        return ResponseEntity.ok(productID);
    }

    @DeleteMapping("/{productID}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable(name = "productID") final UUID productID) {
        final ReferencedWarning referencedWarning = productService.getReferencedWarning(productID);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        productService.delete(productID);
        return ResponseEntity.noContent().build();
    }

}
