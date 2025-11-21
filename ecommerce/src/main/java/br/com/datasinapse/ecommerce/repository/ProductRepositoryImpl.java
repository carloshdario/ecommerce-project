package br.com.datasinapse.ecommerce.repository;

import br.com.datasinapse.ecommerce.model.Product;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public ProductRepositoryImpl(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Product> productRowMapper = (rs, rowNum) -> {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setQuantity(rs.getInt("quantity"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setIsActive(rs.getInt("active") == 1);
        return p;
    };

    @Override
    public Long save(Product product) {
        String sql = "INSERT INTO products (name, description, quantity, price, active) " +
                "VALUES (:name, :description, :quantity, :price, :active)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        Map<String, Object> params = new HashMap<>();
        params.put("name", product.getName());
        params.put("description", product.getDescription());
        params.put("quantity", product.getQuantity());
        params.put("price", product.getPrice());
        params.put("active", product.getIsActive() ? 1 : 0);

        jdbc.update(sql, new MapSqlParameterSource(params), keyHolder, new String[]{"ID"});
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public void update(Product product) {
        String sql = "UPDATE products SET name = :name, description = :description, " +
                "quantity = :quantity, price = :price, active = :active " +
                "WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", product.getId());
        params.put("name", product.getName());
        params.put("description", product.getDescription());
        params.put("quantity", product.getQuantity());
        params.put("price", product.getPrice());
        params.put("active", product.getIsActive() ? 1 : 0);

        jdbc.update(sql, new MapSqlParameterSource(params));
    }

    @Override
    public void softDelete(Long id) {
        String sql = "UPDATE products SET active = 0 WHERE id = :id";
        jdbc.update(sql, new MapSqlParameterSource("id", id));
    }

    @Override
    public Optional<Product> findById(Long id) {
        String sql = "SELECT id, name, description, quantity, price, active FROM products WHERE id = :id";
        try {
            Product product = jdbc.queryForObject(sql, new MapSqlParameterSource("id", id), productRowMapper);
            return Optional.of(product);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM products";
        return Objects.requireNonNull(jdbc.queryForObject(sql, new HashMap<>(), Long.class));
    }

    @Override
    public long countActive() {
        String sql = "SELECT COUNT(*) FROM products WHERE active = 1";
        return Objects.requireNonNull(jdbc.queryForObject(sql, new HashMap<>(), Long.class));
    }

    @Override
    public List<Product> findAllPaged(int offset, int size) {
        String sql = "SELECT id, name, description, quantity, price, active FROM products " +
                "ORDER BY id ASC OFFSET :offset ROWS FETCH NEXT :size ROWS ONLY";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("size", size)
                .addValue("offset", offset);


        return jdbc.query(sql, params, productRowMapper);
    }


    @Override
    public List<Product> findActivePaged(int offset, int size) {
        String sql = "SELECT id, name, description, quantity, price, active FROM products " +
                "WHERE active = 1 ORDER BY id ASC OFFSET :offset ROWS FETCH NEXT :size ROWS ONLY";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("size", size)
                .addValue("offset", offset);

        return jdbc.query(sql, params, productRowMapper);
    }
}