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
 * Класс {@code CustomUserDetails} представляет собой реализацию интерфейса {@link org.springframework.security.core.userdetails.UserDetails},
 * отвечающего за управление правами доступа пользователей.
 * Инкапсулирует объект {@link com.example.StudentEnvironment.entities.User}.
 */
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    /**
     * Объект пользователя.
     */
    private User user;

    /**
     * Возвращает ID пользователя.
     *
     * @return ID пользователя
     */
    public Long getId() {
        return user.getId();
    }

    /**
     * Возвращает коллекцию прав пользователя на основе его роли.
     *
     * @return коллекция прав доступа
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(user.getRole().split(", "))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает пароль пользователя.
     *
     * @return пароль
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Возвращает имя пользователя.
     *
     * @return имя пользователя
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Проверяет, не просрочен ли аккаунт.
     *
     * @return {@code true}, если аккаунт действителен
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Проверяет, не заблокирован ли аккаунт.
     *
     * @return {@code true}, если аккаунт не заблокирован
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Проверяет, не просрочены ли учетные данные.
     *
     * @return {@code true}, если учетные данные действительны
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Проверяет, активен ли аккаунт.
     *
     * @return {@code true}, если аккаунт активен
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
