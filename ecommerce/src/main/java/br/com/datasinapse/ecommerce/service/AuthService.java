package br.com.datasinapse.ecommerce.service;

import br.com.datasinapse.ecommerce.enums.Role;
import br.com.datasinapse.ecommerce.model.User;
import br.com.datasinapse.ecommerce.repository.UserRepository;
import br.com.datasinapse.ecommerce.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Role DEFAULT_ROLE = Role.ROLE_USER;

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }


    public User authenticateAndGetUser(String username, String rawPassword) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        return user;
    }

    public String generateToken(User user) {
        return jwtUtil.generateToken(user.getUsername(), user.getRole());
    }

    public Long register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setRole(DEFAULT_ROLE);

        return userRepo.save(user);
    }
}