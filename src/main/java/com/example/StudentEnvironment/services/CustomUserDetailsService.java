package com.example.StudentEnvironment.services;

import com.example.StudentEnvironment.config.CustomUserDetails;
import com.example.StudentEnvironment.entities.User;
import com.example.StudentEnvironment.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
/**
 * Класс представляет собой реализацию сервиса
 * для работы с правами доступа пользователей.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository repository;
    /**
     * Метод поиска пользователя по имени
     * @param username имя пользователя
     * @return объект UserDetails
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = repository.findByUsername(username);
        return user.map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(username + "не найден"));
    }

}