package teamnova.elite_gear.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamnova.elite_gear.model.SizeDTO;
import teamnova.elite_gear.model.SizeTypeDTO;
import teamnova.elite_gear.service.SizeService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sizes")
@RequiredArgsConstructor
public class SizeResource {
    private final SizeService sizeService;

    @PostMapping
    public ResponseEntity<List<SizeDTO>> createSizes(@RequestBody List<SizeDTO> sizeDTOs) {
        List<SizeDTO> createdSizes = sizeService.createSizes(sizeDTOs);
        return ResponseEntity.ok(createdSizes);
    }


    @PostMapping("/type")
    public ResponseEntity<SizeTypeDTO> createSizeType(@RequestBody SizeTypeDTO sizeTypeDTO) {
        return ResponseEntity.ok(sizeService.createSizeType(sizeTypeDTO));
    }

    @GetMapping("/type")
    public ResponseEntity<List<SizeTypeDTO>> getSizeTypes() {
        return ResponseEntity.ok(sizeService.getSizeTypes());
    }

    @GetMapping
    public ResponseEntity<List<SizeDTO>> getSize() {
        return ResponseEntity.ok(sizeService.getAllSizes());
    }

    @GetMapping("/type/{sizeTypeId}")
    public ResponseEntity<List<SizeDTO>> getSizesByType(@PathVariable UUID sizeTypeId) {
        return ResponseEntity.ok(sizeService.getSizesByType(sizeTypeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SizeDTO> updateSize(@PathVariable UUID id, @RequestBody SizeDTO sizeDTO) {
        return ResponseEntity.ok(sizeService.updateSize(id, sizeDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSize(@PathVariable UUID id) {
        sizeService.deleteSize(id);
        return ResponseEntity.noContent().build();
    }
}

