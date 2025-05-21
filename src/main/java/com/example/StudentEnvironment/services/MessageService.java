package com.example.StudentEnvironment.services;

import com.example.StudentEnvironment.entities.Message;
import com.example.StudentEnvironment.entities.Group;
import com.example.StudentEnvironment.entities.User;
import com.example.StudentEnvironment.repositories.MessageRepository;
import com.example.StudentEnvironment.repositories.GroupRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 * Сервис для работы с сообщениями.
 */
@Service
@AllArgsConstructor
public class MessageService {
    private final UserService userService;
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
     * @return истина или ложь
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
     * Метод получения всех сообщений определенной группы по её ID
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
     * @param message_id ID сообщения
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
    /**
     * Метод получения сообщений для пользователя
     * @param username имя пользователя
     * @return список сообщений
     */
    public List<Message> getMessagesForUser(String username) {
        User user = userService.findByUsername(username);
        return messageRepository.findByGroupId(user.getGroup().getId());
    }
    /**
     * Метод создания сообщения от имени старосты
     * @param text текст сообщения
     * @param username имя пользователя (старосты)
     */
    public void createMessage(String text, String username) {
        User user = userService.findByUsername(username);

        if (!"HEADMAN".equals(user.getRole())) {
            throw new AccessDeniedException("Only headman can create messages");
        }

        Message message = Message.builder()
                .text(text)
                .time(LocalDateTime.now())
                .group(user.getGroup())
                .build();

        messageRepository.save(message);
    }

    /**
     * Метод удаления сообщения, доступен только старосте своей группы
     * @param messageId ID сообщения
     * @param username имя пользователя (старосты)
     */
    public void deleteMessage(Long messageId, String username) {
        User user = userService.findByUsername(username);
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));

        if (!"HEADMAN".equals(user.getRole()) || !user.getGroup().equals(message.getGroup())) {
            throw new AccessDeniedException("No permission to delete this message");
        }

        messageRepository.delete(message);
    }

}
