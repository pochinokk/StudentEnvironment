package com.example.StudentEnvironment.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
/**
 *  Контроллер главной страницы
 */
@Controller
public class HomeController {
    /**
     * Метод отображения главной страницы "/"
     * @param request HTTP-запрос
     * @param model объект Model для передачи данных в представление
     * @return имя представления главной страницы
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
     * Метод отображения альтернативной главной страницы "/home"
     * @param request HTTP-запрос
     * @param model объект Model для передачи данных в представление
     * @return имя представления главной страницы
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
