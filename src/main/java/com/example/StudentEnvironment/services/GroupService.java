package com.example.StudentEnvironment.services;

import com.example.StudentEnvironment.entities.Group;
import com.example.StudentEnvironment.repositories.GroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private PasswordEncoder passwordEncoder;
    /**
     * Метод создания группы в таблице
     * @param name название группы
     */
    public void create(String name) {
        Group group = Group.builder()
                .name(name)
                .build();
        groupRepository.save(group);
    }
    /**
     * Метод проверки существования группы
     * @param name название группы
     * @return иситина или ложь
     */
    public boolean exists(String name) {
        List<Group> groups = groupRepository.findAll();
        for (Group candidate : groups){
            if (candidate.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    /**
     * Метод получения ID группы по названию
     * @param name название группы
     * @return ID
     */
    public Long getIDByName(String name) {
        List<Group> groups = groupRepository.findAll();
        for (Group candidate : groups){
            if (candidate.getName().equals(name)){
                return candidate.getId();
            }
        }
        return (long) -1;
    }
    /**
     * Метод получения всех пользователей из таблицы
     * @return все группы
     */
    public List<Group> readAll() {
        return groupRepository.findAll();
    }
    /**
     * Метод удаления группы из таблицы
     * @param id ID
     */
    public void delete(Long id) {
        groupRepository.deleteById(id);
    }

    public Group findById(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Группа не найдена"));
    }
}
