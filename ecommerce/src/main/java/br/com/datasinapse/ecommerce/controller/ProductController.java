package br.com.datasinapse.ecommerce.controller;

import br.com.datasinapse.ecommerce.dto.PaginatedResponse;
import br.com.datasinapse.ecommerce.dto.PaginationRequest;
import br.com.datasinapse.ecommerce.model.Product;
import br.com.datasinapse.ecommerce.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<Product>> getAllProducts(
            PaginationRequest pagination,
            @RequestParam(value = "includeInactive", defaultValue = "false") boolean includeInactive) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication != null && authentication.getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean finalIncludeInactive = isAdmin && includeInactive;

        PaginatedResponse<Product> productPage = service.findAllProducts(pagination, finalIncludeInactive);

        return ResponseEntity.ok(productPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {

        return service.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }



    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Long id = service.createProduct(product);
        product.setId(id);
        return ResponseEntity.status(201).body(product);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        service.updateProduct(product);
        // O frontend Nuxt usa este endpoint para alterar o status isActive/Inativo
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        // Se este for um soft-delete (isActive=false), o PUT deve ser usado
        // Se for um HARD delete, o DELETE está correto
        service.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
}