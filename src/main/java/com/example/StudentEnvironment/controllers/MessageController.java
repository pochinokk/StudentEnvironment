package com.example.StudentEnvironment.controllers;

import com.example.StudentEnvironment.entities.Message;
import com.example.StudentEnvironment.entities.User;
import com.example.StudentEnvironment.repositories.MessageRepository;
import com.example.StudentEnvironment.services.MessageService;
import com.example.StudentEnvironment.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
/**
 * Контроллер канала сообщений для старосты и студентов
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/channel")
public class MessageController {

    private final MessageRepository messageRepository;
    private final UserService userService;

    /**
     * Метод отображения канала сообщений
     * @param request HTTP-запрос
     * @param model объект Model
     * @param principal текущий пользователь
     * @return имя представления канала
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STUDENT', 'HEADMAN')")
    @GetMapping
    public String showChannel(HttpServletRequest request, Model model, Principal principal) {
        // Получаем имя пользователя из контекста безопасности
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Проверяем, авторизован ли пользователь
        if (username.equals("anonymousUser")) {
            model.addAttribute("username", "Вы не авторизованы");
            return "channel";  // Можно вернуть просто канал с уведомлением об отсутствии авторизации
        }

        // Получаем пользователя по имени из базы данных
        User currentUser = userService.findByUsername(username);

        if (currentUser == null) {
            model.addAttribute("error", "Пользователь не найден");
            return "channel";
        }

        // Получаем сообщения из группы пользователя
        List<Message> messages = messageRepository.findByGroup(currentUser.getGroup());

        // Проверяем, является ли пользователь старостой
        boolean isHeadman = "HEADMAN".equals(currentUser.getRole());

        // Добавляем атрибуты для отображения в модели
        model.addAttribute("username", username);
        model.addAttribute("messages", messages);
        model.addAttribute("isHeadman", isHeadman);
        model.addAttribute("newMessage", new Message());
        model.addAttribute("requestURI", request.getRequestURI());
        return "channel";
    }
    /**
     * Метод добавления сообщения старостой
     * @param message сообщение
     * @param principal текущий пользователь
     * @return редирект на канал
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'HEADMAN')")
    @PostMapping("/add")
    public String addMessage(@ModelAttribute Message message, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());

        if (!"HEADMAN".equals(currentUser.getRole())) {
            return "redirect:/channel?error";
        }

        message.setGroup(currentUser.getGroup());
        message.setTime(LocalDateTime.now());
        messageRepository.save(message);
        return "redirect:/channel";
    }
    /**
     * Метод удаления сообщения старостой
     * @param id идентификатор сообщения
     * @param principal текущий пользователь
     * @return редирект на канал
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'HEADMAN')")
    @PostMapping("/delete/{id}")
    public String deleteMessage(@PathVariable Long id, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());

        if (!"HEADMAN".equals(currentUser.getRole())) {
            return "redirect:/channel?error";
        }

        Message message = messageRepository.findById(id)
                .orElse(null);

        if (message == null || !message.getGroup().equals(currentUser.getGroup())) {
            return "redirect:/channel?error";
        }

        messageRepository.delete(message);
        return "redirect:/channel";
    }
}



