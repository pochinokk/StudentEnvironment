package com.example.StudentEnvironment.config;


import com.example.StudentEnvironment.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Класс представляет собой реализацию интерфейса,
 * отвечающего за управление правами дооступа пользователей.
 */
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    /**
     * Поле содержащее пользователя
     */
    private User user;
    /**
     * Метод получения ID пользователя
     * @return ID пользователя
     */
    public Long getId() {
        return user.getId();
    }
    /**
     * Метод получения прав доступа пользователя
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(user.getRole().split(", "))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
    /**
     * Метод получения пароля пользователя
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    /**
     * Метод получения имени пользователя
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }
    /**
     * Метод проверки просроченности аккаунта
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    /**
     * Метод проверки заблокированности аккаунта
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    /**
     * Метод проверки просроченности данных
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    /**
     * Метод проверки доступности аккаунта
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}