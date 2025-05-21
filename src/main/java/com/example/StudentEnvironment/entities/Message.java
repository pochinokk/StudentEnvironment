package com.example.StudentEnvironment.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/**
 * Сущность сообщения в канале группы
 */
@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "messages")
public class Message {
    /** Идентификатор сообщения */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    /** Текст сообщения */
    @Column(name = "text")
    private String text;
    /** Время отправки сообщения */
    @Column(name = "time")
    private LocalDateTime time;
    /** Группа, к которой относится сообщение */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    @JsonIgnore
    private Group group;
}
