package teamnova.elite_gear.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import teamnova.elite_gear.domain.Customer;
import teamnova.elite_gear.domain.Size;
import teamnova.elite_gear.domain.SizeType;
import teamnova.elite_gear.model.CustomerDTO;
import teamnova.elite_gear.model.SizeDTO;
import teamnova.elite_gear.model.SizeTypeDTO;
import teamnova.elite_gear.repos.SizeRepository;
import teamnova.elite_gear.repos.SizeTypeRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SizeService {
    private final SizeRepository sizeRepository;
    private final SizeTypeRepository sizeTypeRepository;

    public List<SizeDTO> createSizes(List<SizeDTO> sizeDTOs) {
        return sizeDTOs.stream().map(sizeDTO -> {
            Size size = new Size();
            size.setValue(sizeDTO.getValue());

            SizeType sizeType = sizeTypeRepository.findById(sizeDTO.getSizeTypeId())
                    .orElseThrow(() -> new EntityNotFoundException("Size type not found with ID: " + sizeDTO.getSizeTypeId()));
            size.setSizeType(sizeType);

            Size savedSize = sizeRepository.save(size);

            // Convert Entity to DTO
            return convertToDTO(savedSize);
        }).collect(Collectors.toList());
    }


    public SizeTypeDTO createSizeType(SizeTypeDTO sizeTypeDTO) {
        SizeType sizeType = new SizeType();

        sizeType.setName(sizeTypeDTO.getName());
        SizeType savedSizeType = sizeTypeRepository.save(sizeType);
        return convertToDTO(savedSizeType);
    }

    public SizeDTO getSize(UUID id) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Size not found"));
        return convertToDTO(size);
    }

    public List<SizeDTO> getAllSizes() {
       final List<Size> sizes = sizeRepository.findAll(Sort.by("sizeId"));
        return sizes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<SizeTypeDTO> getSizeTypes() {
        return sizeTypeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<SizeDTO> getSizesByType(UUID sizeTypeId) {
        return sizeRepository.findBySizeTypeSizeTypeId(sizeTypeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public SizeDTO updateSize(UUID id, SizeDTO sizeDTO) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Size not found"));
        updateSizeFromDTO(size, sizeDTO);
        Size updatedSize = sizeRepository.save(size);
        return convertToDTO(updatedSize);
    }

    public void deleteSize(UUID id) {
        sizeRepository.deleteById(id);
    }

    private void updateSizeFromDTO(Size size, SizeDTO dto) {
        SizeType sizeType = sizeTypeRepository.findById(dto.getSizeTypeId())
                .orElseThrow(() -> new RuntimeException("Size type not found"));

        size.setValue(dto.getValue());
        size.setSizeType(sizeType);
    }


    private SizeDTO convertToDTO(Size size) {
        SizeDTO dto = new SizeDTO();
        dto.setSizeId(size.getSizeId());
        dto.setValue(size.getValue());
        dto.setSizeTypeId(size.getSizeType().getSizeTypeId());
        return dto;
    }
    private SizeTypeDTO convertToDTO(SizeType sizeType) {
        SizeTypeDTO dto = new SizeTypeDTO();
        dto.setSizeTypeId(sizeType.getSizeTypeId());
        dto.setName(sizeType.getName());
        return dto;
    }
}