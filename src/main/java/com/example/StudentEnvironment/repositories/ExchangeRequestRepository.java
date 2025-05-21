package com.example.StudentEnvironment.repositories;

import com.example.StudentEnvironment.entities.ExchangeRequest;
import com.example.StudentEnvironment.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {
    boolean existsByFromUser(User fromUser);

    void deleteByFromUser(User fromUser);

    void deleteByFromUserOrToUser(User user1, User user2);

    List<ExchangeRequest> findByToUser(User toUser);
}
