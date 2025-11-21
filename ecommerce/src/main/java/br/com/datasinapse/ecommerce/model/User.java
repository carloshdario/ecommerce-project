package br.com.datasinapse.ecommerce.model;

import br.com.datasinapse.ecommerce.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

public class User implements UserDetails {

    private Long id;
    private String name;
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private Role role = Role.ROLE_USER;
    private boolean isActive = true;

    public User() {}

    public User(Long id, String name, String username, String password, Role role, boolean isActive) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // pode colocar regra depois se quiser
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // pode colocar regra depois se quiser
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // pode colocar regra depois se quiser
    }

    @Override
    public boolean isEnabled() {
        return isActive; // controla ativo/inativo (soft delete)
    }
}
