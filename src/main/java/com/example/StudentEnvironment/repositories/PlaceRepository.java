package com.example.StudentEnvironment.repositories;

import com.example.StudentEnvironment.entities.Place;
import com.example.StudentEnvironment.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Репозиторий для работы с сущностью Place
 */
@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    /**
     * Метод для поиска места по пользователю
     * @param user пользователь
     * @return объект Place
     */
    Place findByUser(User user);

    /**
     * Метод для получения максимального времени из всех мест
     * @return максимальное время
     */
    @Query("SELECT MAX(p.time) FROM Place p")
    LocalDateTime findMaxTime();
}
