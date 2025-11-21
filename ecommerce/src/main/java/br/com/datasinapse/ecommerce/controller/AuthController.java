package br.com.datasinapse.ecommerce.controller;

import br.com.datasinapse.ecommerce.dto.LoginRequest;
import br.com.datasinapse.ecommerce.dto.LoginResponse;
import br.com.datasinapse.ecommerce.dto.UserDto; // Novo Import
import br.com.datasinapse.ecommerce.model.User;
import br.com.datasinapse.ecommerce.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        User authenticatedUser = authService.authenticateAndGetUser(request.getUsername(), request.getPassword());

        String token = authService.generateToken(authenticatedUser);

        UserDto userDto = new UserDto(
                authenticatedUser.getRole().name()
        );

        return new LoginResponse(token, userDto);
    }


    @PostMapping("/register")
    public Long register(@RequestBody User user) {
        return authService.register(user);
    }
}