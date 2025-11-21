package br.com.datasinapse.ecommerce.repository;


import br.com.datasinapse.ecommerce.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    Long save(User user);
    List<User> findAll();
    void softDelete(Long id);
}