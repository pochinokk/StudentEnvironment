package com.example.StudentEnvironment.repositories;

import com.example.StudentEnvironment.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
/**
 * Репозиторий для работы с сущностью User
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Метод поиска пользователя по имени
     * @param username имя пользователя
     * @return объект Optional с пользователем
     */
    Optional<User> findByUsername(String username);
}
