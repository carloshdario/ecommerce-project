package br.com.datasinapse.ecommerce.controller;

import br.com.datasinapse.ecommerce.dto.OrderHistoryDto; // 💡 NOVO: Importa o DTO de histórico
import br.com.datasinapse.ecommerce.repository.UserRepository;
import br.com.datasinapse.ecommerce.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepo;

    public OrderController(OrderService orderService, UserRepository userRepo) {
        this.orderService = orderService;
        this.userRepo = userRepo;
    }

    private Long getLoggedInUserId() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado no contexto de segurança"))
                .getId();
    }


    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    public ResponseEntity<Void> makeOrder(@RequestBody Map<String, Object> orderRequest) {

        Long productId = ((Number) orderRequest.get("productId")).longValue();
        int quantity = (Integer) orderRequest.get("quantity");

        Long userId = getLoggedInUserId();

        orderService.makeOrder(userId, productId, quantity);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/history")
    public ResponseEntity<List<OrderHistoryDto>> getHistory() {

        Long userId = getLoggedInUserId();

        List<OrderHistoryDto> history = orderService.getUserHistory(userId);

        return ResponseEntity.ok(history);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/all-history")
    public ResponseEntity<List<OrderHistoryDto>> getAllHistory() {

        List<OrderHistoryDto> allHistory = orderService.getAllUsersHistory();

        return ResponseEntity.ok(allHistory);
    }
}