package ru.effectivemobile.testtask.web.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.effectivemobile.testtask.model.Client;

import java.util.Collection;
import java.util.List;

/**
 * Представление User для Spring Security.
 * Предоставляет информацию о пользователе.
 */
@Data
@AllArgsConstructor
public class JwtEntity implements UserDetails {

    private Client client;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return client.getPassword();
    }

    @Override
    public String getUsername() {
        return client.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}