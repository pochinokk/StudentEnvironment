package com.example.StudentEnvironment.repositories;

import com.example.StudentEnvironment.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * Репозиторий для работы с сущностью Group
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
}
