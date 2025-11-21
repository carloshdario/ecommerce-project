package br.com.datasinapse.ecommerce.service;

import br.com.datasinapse.ecommerce.dto.PaginatedResponse;
import br.com.datasinapse.ecommerce.dto.PaginationRequest;
import br.com.datasinapse.ecommerce.model.Product;
import br.com.datasinapse.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Long createProduct(Product product) {
        return repository.save(product);
    }

    public void updateProduct(Product product) {
        repository.update(product);
    }

    public void deleteProduct(Long id) {
        repository.softDelete(id);
    }

    public Optional<Product> getProductById(Long id) {
        return repository.findById(id);
    }

    public PaginatedResponse<Product> findAllProducts(PaginationRequest pagination, boolean includeInactive) {

        long totalItems;
        if (includeInactive) {
            totalItems = repository.countAll();
        } else {
            totalItems = repository.countActive(); // 💡 Implementar countActive() no Repositorio
        }

        List<Product> products;
        if (includeInactive) {
            products = repository.findAllPaged(pagination.getOffset(), pagination.getSize());
        } else {
            products = repository.findActivePaged(pagination.getOffset(), pagination.getSize());
        }

        return new PaginatedResponse<>(products, totalItems, pagination);
    }
}