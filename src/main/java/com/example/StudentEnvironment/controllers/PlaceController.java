package com.example.StudentEnvironment.controllers;

import com.example.StudentEnvironment.entities.User;
import com.example.StudentEnvironment.services.PlaceService;
import com.example.StudentEnvironment.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
/**
 * Контроллер управления очередью (местами)
 */
@Controller
@RequestMapping("/places")
public class PlaceController {

    private final PlaceService placeService;
    private final UserService userService;
    /**
     * Конструктор контроллера
     * @param placeService сервис для работы с местами
     * @param userService сервис для работы с пользователями
     */
    @Autowired
    public PlaceController(PlaceService placeService, UserService userService) {
        this.placeService = placeService;
        this.userService = userService;
    }

    /**
     * Метод удаления места
     * @param placeId идентификатор места
     * @param principal текущий пользователь
     * @return редирект на страницу группы
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STUDENT', 'HEADMAN')")
    @PostMapping("/delete/{placeId}")
    public String deletePlace(@PathVariable Long placeId, Principal principal) {
        System.out.println("Удаляем место");
        User user = userService.findByUsername(principal.getName());
        placeService.deletePlace(placeId, user);
        return "redirect:/groups/" + user.getGroup().getId();
    }


    /**
     * Метод постановки пользователя в конец очереди
     * @param principal текущий пользователь
     * @param redirectAttributes атрибуты для передачи сообщений
     * @return редирект на страницу группы
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STUDENT', 'HEADMAN')")
    @PostMapping("/add-to-end")
    public String addToEnd(Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(principal.getName());

        try {
            placeService.addToEnd(user);
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/groups/" + user.getGroup().getId();
    }

    /**
     * Метод запроса обмена местами
     * @param placeId идентификатор места
     * @param principal текущий пользователь
     * @return редирект на страницу группы
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STUDENT', 'HEADMAN')")
    @PostMapping("/exchange-request/{placeId}")
    public String requestExchange(@PathVariable Long placeId, Principal principal) {
        User fromUser = userService.findByUsername(principal.getName());
        placeService.requestExchange(fromUser, placeId);
        return "redirect:/groups/" + fromUser.getGroup().getId();
    }

    /**
     * Метод вставки студента перед другим студентом (только для старосты)
     * @param placeId идентификатор целевого места
     * @param studentId идентификатор вставляемого студента
     * @param principal текущий пользователь
     * @return редирект на страницу группы
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'HEADMAN')")
    @PostMapping("/insert-before/{placeId}")
    public String insertBefore(@PathVariable Long placeId,
                               @RequestParam Long studentId,
                               Principal principal) {
        User headman = userService.findByUsername(principal.getName());
        placeService.insertBefore(placeId, studentId, headman);
        return "redirect:/groups/" + headman.getGroup().getId();
    }
    /**
     * Метод добавления студента в конец очереди старостой
     * @param studentId идентификатор студента
     * @param principal текущий пользователь
     * @param redirectAttributes атрибуты для передачи сообщений
     * @return редирект на страницу группы
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'HEADMAN')")
    @PostMapping("/add-student-to-end")
    public String addStudentToEnd(@RequestParam Long studentId, Principal principal, RedirectAttributes redirectAttributes) {
        User headman = userService.findByUsername(principal.getName());
        User student = userService.findById(studentId);

        try {
            placeService.addToEnd(student); // добавляем студента, а не headman'а
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/groups/" + headman.getGroup().getId();
    }
}

