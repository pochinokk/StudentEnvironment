package com.example.StudentEnvironment.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/**
 * Сущность места в очереди
 */
@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "places")
public class Place {
    /** Идентификатор места */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    /** Время добавления в очередь */
    @Column(name = "time")
    private LocalDateTime time;

    /** Пользователь, занимающий место */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}
