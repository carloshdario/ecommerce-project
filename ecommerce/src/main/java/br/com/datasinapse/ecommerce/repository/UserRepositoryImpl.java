package br.com.datasinapse.ecommerce.repository;

import br.com.datasinapse.ecommerce.enums.Role;
import br.com.datasinapse.ecommerce.model.User;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public UserRepositoryImpl(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, password, name, role, active " +
                "FROM users WHERE username = :username AND active = 1";

        MapSqlParameterSource params = new MapSqlParameterSource("username", username);

        try {
            User u = jdbc.queryForObject(sql, params, (rs, rn) -> {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setName(rs.getString("name"));
                user.setRole(Role.valueOf(rs.getString("role")));
                user.setActive(rs.getInt("active") == 1);
                return user;
            });
            return Optional.of(u);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Long save(User user) {
        String sql = "INSERT INTO users (username, password, name, role, active) " +
                "VALUES (:username, :password, :name, :role, :active)";

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        Map<String, Object> params = new HashMap<>();
        params.put("username", user.getUsername());
        params.put("password", user.getPassword());
        params.put("name", user.getName());
        params.put("role", user.getRole().name());
        params.put("active", user.isActive() ? 1 : 0);

        jdbc.update(sql, new MapSqlParameterSource(params), keyHolder, new String[]{"ID"});

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT id, username, name, role, active " +
                "FROM users WHERE active = 1";

        return jdbc.query(sql, (rs, rn) -> {
            User u = new User();
            u.setId(rs.getLong("id"));
            u.setUsername(rs.getString("username"));
            u.setName(rs.getString("name"));
            u.setRole(Role.valueOf(rs.getString("role")));
            u.setActive(rs.getInt("active") == 1);
            return u;
        });
    }

    @Override
    public void softDelete(Long id) {
        String sql = "UPDATE users SET active = 0 WHERE id = :id";
        jdbc.update(sql, new MapSqlParameterSource("id", id));
    }
}
