package teamnova.elite_gear.rest;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
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
import teamnova.elite_gear.model.CategoryDTO;
import teamnova.elite_gear.service.CategoryService;
import teamnova.elite_gear.util.ReferencedException;
import teamnova.elite_gear.util.ReferencedWarning;


@RestController
@RequestMapping(value = "/api/categories", produces = MediaType.APPLICATION_JSON_VALUE)
public class CategoryResource {

    private final CategoryService categoryService;

    public CategoryResource(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/{orderItemID}")
    public ResponseEntity<CategoryDTO> getCategory(
            @PathVariable(name = "orderItemID") final UUID orderItemID) {
        return ResponseEntity.ok(categoryService.get(orderItemID));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<UUID> createCategory(@RequestBody @Valid final CategoryDTO categoryDTO) {
        final UUID createdOrderItemID = categoryService.create(categoryDTO);
        return new ResponseEntity<>(createdOrderItemID, HttpStatus.CREATED);
    }

    @PutMapping("/{orderItemID}")
    public ResponseEntity<UUID> updateCategory(
            @PathVariable(name = "orderItemID") final UUID orderItemID,
            @RequestBody @Valid final CategoryDTO categoryDTO) {
        categoryService.update(orderItemID, categoryDTO);
        return ResponseEntity.ok(orderItemID);
    }

    @DeleteMapping("/{orderItemID}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable(name = "orderItemID") final UUID orderItemID) {
        final ReferencedWarning referencedWarning = categoryService.getReferencedWarning(orderItemID);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        categoryService.delete(orderItemID);
        return ResponseEntity.noContent().build();
    }

}
