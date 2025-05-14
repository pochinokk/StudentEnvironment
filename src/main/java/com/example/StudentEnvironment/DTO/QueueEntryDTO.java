package com.example.StudentEnvironment.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/**
 * Класс DTO для представления записи в очереди
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueueEntryDTO {
    /** Идентификатор места */
    private Long placeId;
    /** Полное имя пользователя */
    private String fullName;
    /** Время добавления в очередь */
    private LocalDateTime time;
    /** Идентификатор пользователя */
    private Long userId;
    /**
     * Конструктор без идентификаторов
     * @param fullName полное имя пользователя
     * @param time время добавления
     */
    public QueueEntryDTO(String fullName, LocalDateTime time) {
        this.fullName = fullName;
        this.time = time;
    }
}