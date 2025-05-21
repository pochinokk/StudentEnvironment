package com.example.StudentEnvironment.controllers;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер {@code AboutController} обрабатывает запросы к странице "О нас".
 * Отображает имя текущего пользователя и URI запроса.
 */
@Controller
public class AboutController {

    /**
     * Обрабатывает GET-запрос к странице "О нас".
     *
     * @param request HTTP-запрос
     * @param model модель данных для отображения
     * @return имя шаблона страницы
     */
    @GetMapping("/about_us")
    public String about(HttpServletRequest request, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (username.equals("anonymousUser")) {
            username = "Вы не авторизованы";
        }
        model.addAttribute("username", username);
        model.addAttribute("requestURI", request.getRequestURI());
        return "about_us_page";
    }
}
