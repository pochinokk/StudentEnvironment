package com.example.StudentEnvironment.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
/**
 * Класс представляет собой контроллер.
 * Обрабатывает запросы на предоставление страницы главной страницы.
 */
@Controller
public class HomeController {
    /**
     * Метод получения главной страницы
     * @param model объект Model, содержащий атрибуты для рендеринга представления
     * @return имя представления для главной страницы
     */
    @GetMapping("/")
    public String home(HttpServletRequest request, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (username.equals("anonymousUser")) {
            username = "Вы не авторизованы";
        }
        model.addAttribute("username", username);
        model.addAttribute("requestURI", request.getRequestURI());
        System.out.println(request.getRequestURI());
        return "index";
    }
    /**
     * Метод получения главной страницы
     * @param model объект Model, содержащий атрибуты для рендеринга представления
     * @return имя представления для главной страницы
     */
    @GetMapping("/home")
    public String home2(HttpServletRequest request, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (username.equals("anonymousUser")) {
            username = "Вы не авторизованы";
        }
        model.addAttribute("username", username);
        model.addAttribute("requestURI", request.getRequestURI());
        return "index";
    }
}
