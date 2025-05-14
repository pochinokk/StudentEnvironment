package com.example.StudentEnvironment.services;

import com.example.StudentEnvironment.entities.Group;
import com.example.StudentEnvironment.entities.User;
import com.example.StudentEnvironment.repositories.GroupRepository;
import com.example.StudentEnvironment.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * Сервис для работы с пользователями.
 */
@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private PasswordEncoder passwordEncoder;
    /**
     * Метод создания пользователя в таблице
     * @param username логин
     * @param password пароль
     * @param full_name ФИО
     * @param role роль
     * @param group_id ID группы
     */
    public void create(String username, String password, String full_name, String role, Long group_id) {
        Group group = groupRepository.findById(group_id)
                .orElseThrow(() -> new RuntimeException("Группа не найдена"));
        User user = User.builder()
                .username(username)
                .password(password)
                .full_name(full_name)
                .role(role)
                .group(group)
                .build();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
    /**
     * Метод проверки существования пользователя
     * @param username логин
     * @return истина или ложь
     */
    public boolean exists(String username) {
        List<User> users = userRepository.findAll();
        for (User candidate : users){
            if (candidate.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }

    /**
     * Метод получения ID пользователя по имени
     * @param username логин
     * @return ID
     */
    public Long getIDByName(String username) {
        List<User> users = userRepository.findAll();
        for (User candidate : users){
            if (candidate.getUsername().equals(username)){
                return candidate.getId();
            }
        }
        return (long) -1;
    }
    /**
     * Метод получения всех пользователей
     * @return список пользователей
     */
    public List<User> readAll() {
        return userRepository.findAll();
    }
    /**
     * Метод удаления пользователя по ID
     * @param id ID
     */
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
    /**
     * Метод изменения пароля пользователя
     * @param username логин
     * @param newPassword новый пароль
     */
    public void changePassword(String username, String newPassword) {
        List<User> users = userRepository.findAll();
        User user = null;
        for (User candidate : users){
            if (candidate.getUsername().equals(username)){
                user = candidate;
                break;
            }
        }
        if (user != null) {
            System.out.println(user.getUsername());
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            System.out.println("Пользователь не найден");
        }
    }
    /**
     * Метод получения пользователя по ID
     * @param userId ID пользователя
     * @return пользователь
     */
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }
    /**
     * Метод получения пользователя по имени
     * @param username логин
     * @return пользователь
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }
}
