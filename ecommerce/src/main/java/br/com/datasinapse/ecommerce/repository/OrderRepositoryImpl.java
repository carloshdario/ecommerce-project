package br.com.datasinapse.ecommerce.repository;

import br.com.datasinapse.ecommerce.dto.OrderHistoryDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public OrderRepositoryImpl(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void registerOrder(Long userId, Long productId, int quantity) {
        String sql = "BEGIN register_order(:userId, :productId, :quantity); END;";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("productId", productId)
                .addValue("quantity", quantity);

        jdbc.update(sql, params);
    }


    @Override
    public List<OrderHistoryDto> getUserHistory(Long userId) {
        String sql = "SELECT o.id AS order_id, " +
                "p.name AS product_name, " +
                "o.quantity, o.price, o.purchase_date " +
                "FROM orders o " +
                "JOIN products p ON p.id = o.product_id " +
                "WHERE o.user_id = :userId " +
                "ORDER BY o.purchase_date DESC";

        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);

        return jdbc.query(sql, params, new OrderHistoryRowMapper());
    }


    @Override
    public List<OrderHistoryDto> findAllOrders() {
        String sql = "SELECT o.id AS order_id, " +
                "p.name AS product_name, " +
                "o.quantity, o.price, o.purchase_date, " +
                "u.username AS customer_username " +
                "FROM orders o " +
                "JOIN products p ON p.id = o.product_id " +
                "JOIN users u ON u.id = o.user_id " +
                "ORDER BY o.purchase_date DESC";

        return jdbc.query(sql, new AdminOrderHistoryRowMapper());
    }


    private static final class OrderHistoryRowMapper implements RowMapper<OrderHistoryDto> {
        @Override
        public OrderHistoryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            OrderHistoryDto dto = new OrderHistoryDto();

            dto.setOrder_id(rs.getLong("order_id"));
            dto.setProduct_name(rs.getString("product_name"));
            dto.setQuantity(rs.getInt("quantity"));
            dto.setPrice(rs.getBigDecimal("price"));
            dto.setPurchase_date(rs.getObject("purchase_date", LocalDateTime.class));


            return dto;
        }
    }


    private static final class AdminOrderHistoryRowMapper implements RowMapper<OrderHistoryDto> {
        @Override
        public OrderHistoryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            OrderHistoryDto dto = new OrderHistoryDto();

            dto.setOrder_id(rs.getLong("order_id"));
            dto.setProduct_name(rs.getString("product_name"));
            dto.setQuantity(rs.getInt("quantity"));
            dto.setPrice(rs.getBigDecimal("price"));
            dto.setPurchase_date(rs.getObject("purchase_date", LocalDateTime.class));

            dto.setCustomer_username(rs.getString("customer_username"));

            return dto;
        }
    }
}