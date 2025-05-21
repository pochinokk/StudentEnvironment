package com.example.StudentEnvironment.controllers;
import com.example.StudentEnvironment.DTO.QueueEntryDTO;
import com.example.StudentEnvironment.entities.Group;
import com.example.StudentEnvironment.entities.Place;
import com.example.StudentEnvironment.entities.User;
import com.example.StudentEnvironment.services.GroupService;
import com.example.StudentEnvironment.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
/**
 * Контроллер управления группами и отображением очередей.
 */
@Controller
@AllArgsConstructor
public class GroupController {
    private UserService userService;
    private GroupService groupService;
    /**
     * Отображение страницы со списком групп.
     *
     * @param request HTTP-запрос
     * @param model модель представления
     * @return имя представления: groups_page
     */
    @GetMapping("/groups")
    public String groups(HttpServletRequest request, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (username.equals("anonymousUser")) {
            username = "Вы не авторизованы";
        }
        model.addAttribute("username", username);
        model.addAttribute("requestURI", request.getRequestURI());
        List<Group> groups = groupService.readAll(); // <-- добавляем группы
        model.addAttribute("groups", groups);        // <-- передаём в модель

        return "groups_page";
    }

    /**
     * Отображение страницы очереди конкретной группы.
     *
     * @param groupId ID группы
     * @param model модель представления
     * @param session HTTP-сессия
     * @return имя представления: queue_page
     */
    @GetMapping("/groups/{groupId}")
    public String getGroupQueue(@PathVariable Long groupId,
                                Model model,
                                HttpSession session) {
        Group group = groupService.findById(groupId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (username.equals("anonymousUser")) {
            username = "Вы не авторизованы";
        }
        model.addAttribute("username", username);

        // Для шаблона Thymeleaf: если не авторизован — роль "GUEST"
        String role = "GUEST";
        Long userId = -1L;

        if (!username.equals("Вы не авторизованы")) {
            Long id = userService.getIDByName(username);
            User user = userService.findById(id);
            role = user.getRole().toString(); // предполагается, что роль — это enum
            userId = user.getId();
        }

        System.out.println(role);
        System.out.println(userId);
        System.out.println(group.getName());
        model.addAttribute("role", role);
        model.addAttribute("userId", userId); // понадобится в шаблоне
        model.addAttribute("group_name", group.getName());

        List<QueueEntryDTO> queue = new ArrayList<>();
        for (User u : group.getUsers()) {
            List<Place> places = u.getPlaces();
            if (!places.isEmpty())
            {
                Place place = u.getPlaces().get(0);
                if (place != null) {
                    queue.add(new QueueEntryDTO(
                            place.getId(),
                            u.getFull_name(),
                            place.getTime(),
                            u.getId()
                    ));
                }
            }

        }


        queue.sort(Comparator.comparing(QueueEntryDTO::getTime));
        model.addAttribute("queue", queue);

        // Добавим список студентов группы, если пользователь — староста
        if ("HEADMAN".equals(role)) {
            model.addAttribute("students", group.getUsers());
        }
        final Long finalUserId = userId;
        boolean isInGroup = group.getUsers().stream()
                .anyMatch(u -> u.getId().equals(finalUserId));

        model.addAttribute("isInGroup", isInGroup);

        return "queue_page";
    }



}
