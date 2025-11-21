package br.com.datasinapse.ecommerce.repository;

import br.com.datasinapse.ecommerce.dto.OrderHistoryDto; // 💡 NOVO IMPORT

import java.util.List;

public interface OrderRepository {
    void registerOrder(Long userId, Long productId, int quantity);
    List<OrderHistoryDto> getUserHistory(Long userId);
    List<OrderHistoryDto> findAllOrders();
}