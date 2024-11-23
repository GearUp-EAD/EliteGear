package teamnova.elite_gear.service;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import teamnova.elite_gear.domain.Category;
import teamnova.elite_gear.domain.OrderItem;
import teamnova.elite_gear.domain.Product;
import teamnova.elite_gear.model.ProductDTO;
import teamnova.elite_gear.repos.CategoryRepository;
import teamnova.elite_gear.repos.OrderItemRepository;
import teamnova.elite_gear.repos.ProductRepository;
import teamnova.elite_gear.util.NotFoundException;
import teamnova.elite_gear.util.ReferencedWarning;


@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderItemRepository orderItemRepository;

    public ProductService(final ProductRepository productRepository,
            final CategoryRepository categoryRepository,
            final OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public List<ProductDTO> findAll() {
        final List<Product> products = productRepository.findAll(Sort.by("productID"));
        return products.stream()
                .map(product -> mapToDTO(product, new ProductDTO()))
                .toList();
    }

    public ProductDTO get(final UUID productID) {
        return productRepository.findById(productID)
                .map(product -> mapToDTO(product, new ProductDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public List<ProductDTO> findByCategory(final UUID categoryID) {
        final List<Product> products = productRepository.findByCategoryOrderByName(categoryRepository.findById(categoryID)
                .orElseThrow(() -> new NotFoundException("category not found")));
        return products.stream()
                .map(product -> mapToDTO(product, new ProductDTO()))
                .toList();
    }

    public UUID create(final ProductDTO productDTO) {
        final Product product = new Product();
        mapToEntity(productDTO, product);
        return productRepository.save(product).getProductID();
    }

    public void update(final UUID productID, final ProductDTO productDTO) {
        final Product product = productRepository.findById(productID)
                .orElseThrow(NotFoundException::new);
        mapToEntity(productDTO, product);
        productRepository.save(product);
    }

    public void delete(final UUID productID) {
        productRepository.deleteById(productID);
    }

    private ProductDTO mapToDTO(final Product product, final ProductDTO productDTO) {
        productDTO.setProductID(product.getProductID());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setStockQuantity(product.getStockQuantity());
        productDTO.setCategory(product.getCategory() == null ? null : product.getCategory().getOrderItemID());
        return productDTO;
    }

    private Product mapToEntity(final ProductDTO productDTO, final Product product) {
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStockQuantity(productDTO.getStockQuantity());
        final Category category = productDTO.getCategory() == null ? null : categoryRepository.findById(productDTO.getCategory())
                .orElseThrow(() -> new NotFoundException("category not found"));
        product.setCategory(category);
        return product;
    }

    public ReferencedWarning getReferencedWarning(final UUID productID) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Product product = productRepository.findById(productID)
                .orElseThrow(NotFoundException::new);
        final OrderItem productOrderItem = orderItemRepository.findFirstByProduct(product);
        if (productOrderItem != null) {
            referencedWarning.setKey("product.orderItem.product.referenced");
            referencedWarning.addParam(productOrderItem.getOrderItemID());
            return referencedWarning;
        }
        return null;
    }

}
