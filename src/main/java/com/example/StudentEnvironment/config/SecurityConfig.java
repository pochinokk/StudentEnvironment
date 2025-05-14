package com.example.StudentEnvironment.config;

import com.example.StudentEnvironment.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Класс {@code SecurityConfig} отвечает за настройку Spring Security.
 * Генерирует необходимые бины для аутентификации, авторизации и хеширования паролей.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Создаёт сервис, предоставляющий информацию о пользователях для Spring Security.
     *
     * @return реализация {@link UserDetailsService}
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    /**
     * Конфигурирует цепочку фильтров безопасности для обработки HTTP-запросов.
     *
     * @param http объект конфигурации безопасности
     * @return цепочка фильтров безопасности
     * @throws Exception в случае ошибки конфигурации
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.requestMatchers(new AntPathRequestMatcher("/**"))
                        .permitAll().anyRequest().permitAll())
                .formLogin(login -> login
                        .loginPage("/authentication")
                        .loginProcessingUrl("/authenticate")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/home")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .build();
    }

    /**
     * Определяет провайдер аутентификации с использованием DAO и хеширования паролей.
     *
     * @return провайдер аутентификации
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Определяет механизм хеширования паролей с использованием {@link BCryptPasswordEncoder}.
     *
     * @return энкодер паролей
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
