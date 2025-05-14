package com.example.StudentEnvironment.repositories;

import com.example.StudentEnvironment.entities.Group;
import com.example.StudentEnvironment.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * Репозиторий для работы с сущностью Message
 */
public interface MessageRepository extends JpaRepository<Message, Long> {
    /**
     * Метод для получения сообщений по объекту группы
     * @param group объект группы
     * @return список сообщений
     */
    List<Message> findByGroup(Group group);
    /**
     * Метод для получения сообщений по ID группы
     * @param groupId идентификатор группы
     * @return список сообщений
     */
    List<Message> findByGroupId(Long groupId);
}
