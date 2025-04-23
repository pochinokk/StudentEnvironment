package com.example.StudentEnvironment.services;

import com.example.StudentEnvironment.entities.Message;
import com.example.StudentEnvironment.entities.Group;
import com.example.StudentEnvironment.repositories.MessageRepository;
import com.example.StudentEnvironment.repositories.GroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MessageService {
    private final GroupRepository groupRepository;
    private final MessageRepository messageRepository;
    /**
     * Метод создания сообщения в таблице
     * @param text текст
     * @param time время
     * @param group_id ID группы
     */
    public void create(String text, LocalDateTime time, Long group_id) {
        Group group = groupRepository.findById(group_id)
                .orElseThrow(() -> new RuntimeException("Группа не найдена"));
        Message message = Message.builder()
                .text(text)
                .time(time)
                .group(group)
                .build();
        messageRepository.save(message);
    }


    /**
     * Метод проверки существования сообщения
     * @param id ID сообщения
     * @return иситина или ложь
     */
    public boolean exists(Long id) {
        List<Message> messages = messageRepository.findAll();
        for (Message message : messages){
            if (message.getId().equals(id)){
                return true;
            }
        }
        return false;
    }


    /**
     * Метод получения всех сообщений
     * @return список всех сообщений
     */
    public List<Message> readAll() {
        return messageRepository.findAll();
    }
    /**
     * Метод получения всех сообщений определенного группы по его ID
     * @param id ID группы
     * @return список сообщений группы
     */
    public List<Message> readAllByGroupId(Long id) {
        List<Message> messages = messageRepository.findAll();
        List<Message> filtered_messages = new ArrayList<>();
        for(Message message : messages)
        {
            if(message.getGroup().getId().equals(id))
            {
                filtered_messages.add(message);
            }
        }
        return filtered_messages;
    }
    /**
     * Метод получения сообщения по ID
     * @param message_id объект, содержащий ID сообщения
     * @return сообщение
     */
    public Message getMessageByID(Long message_id){
        List<Message> messages = messageRepository.findAll();
        for (Message message : messages){
            if (message.getId().equals(message_id)){
                return message;
            }
        }
        return null;
    }
    /**
     * Метод удаления сообщения по ID
     * @param id ID сообщения
     */
    public void delete(Long id) {messageRepository.deleteById(id);}
}
