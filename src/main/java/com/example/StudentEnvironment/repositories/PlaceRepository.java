package com.example.StudentEnvironment.repositories;

import com.example.StudentEnvironment.entities.Place;
import com.example.StudentEnvironment.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    Place findByUser(User user);

    @Query("SELECT MAX(p.time) FROM Place p")
    LocalDateTime findMaxTime();
}
