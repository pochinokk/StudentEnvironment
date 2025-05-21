package com.example.StudentEnvironment.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
/**
 * Контроллер аутентификации пользователей.
 */
@Controller
public class AuthenticationController {
    /**
     * Отображение страницы аутентификации.
     *
     * @param request HTTP-запрос
     * @param model модель представления
     * @return имя представления: authentication_page
     */
    @GetMapping("/authentication")
    public String authentication(HttpServletRequest request, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (username.equals("anonymousUser")) {
            username = "Вы не авторизованы";
        }
        model.addAttribute("username", username);
        model.addAttribute("requestURI", request.getRequestURI());
        return "authentication_page";
    }
}