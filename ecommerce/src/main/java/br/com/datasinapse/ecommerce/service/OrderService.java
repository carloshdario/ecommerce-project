package br.com.datasinapse.ecommerce.service;

import br.com.datasinapse.ecommerce.dto.OrderHistoryDto; // 💡 NOVO IMPORT
import br.com.datasinapse.ecommerce.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public void makeOrder(Long userId, Long productId, int quantity) {
        repository.registerOrder(userId, productId, quantity);
    }

    public List<OrderHistoryDto> getUserHistory(Long userId) {
        return repository.getUserHistory(userId);
    }

    public List<OrderHistoryDto> getAllUsersHistory() {
        return repository.findAllOrders();
    }
}