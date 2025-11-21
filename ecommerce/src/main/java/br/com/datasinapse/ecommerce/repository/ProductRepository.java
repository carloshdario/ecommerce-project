package br.com.datasinapse.ecommerce.repository;

import br.com.datasinapse.ecommerce.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Long save(Product product);
    void update(Product product);
    void softDelete(Long id);
    Optional<Product> findById(Long id);
    long countAll();
    long countActive();
    List<Product> findAllPaged(int offset, int size);
    List<Product> findActivePaged(int offset, int size);
}