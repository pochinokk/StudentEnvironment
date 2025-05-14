package com.example.StudentEnvironment.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 * Сущность пользователя
 */
@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    /** Идентификатор пользователя */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    /** Уникальное имя пользователя */
    @Column(unique = true, name = "username")
    private String username;
    /** Пароль */
    @Column(name = "password")
    private String password;
    /** Полное имя пользователя */
    @Column(name = "full_name")
    private String full_name;
    /** Роль пользователя (STUDENT, HEADMAN, ADMIN) */
    @Column(name = "role")
    private String role;
    /** Пользователь, с которым предложен обмен местами */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_user_id")
    private User exchange_user;
    /** Группа пользователя */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    @JsonIgnore
    private Group group;
    /** Место в очереди, связанное с пользователем */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    private List<Place> places;
}
