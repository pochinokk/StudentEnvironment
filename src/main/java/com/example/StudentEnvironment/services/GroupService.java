package com.example.StudentEnvironment.services;

import com.example.StudentEnvironment.entities.Group;
import com.example.StudentEnvironment.repositories.GroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * Сервис для управления сущностями Group
 */
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
     * @return истина, если группа существует
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
     * @return ID группы, либо -1 если не найдена
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
     * Метод получения всех групп
     * @return список всех групп
     */
    public List<Group> readAll() {
        return groupRepository.findAll();
    }
    /**
     * Метод удаления группы по ID
     * @param id идентификатор группы
     */
    public void delete(Long id) {
        groupRepository.deleteById(id);
    }
    /**
     * Метод поиска группы по ID
     * @param groupId идентификатор группы
     * @return объект Group
     */
    public Group findById(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Группа не найдена"));
    }
}
