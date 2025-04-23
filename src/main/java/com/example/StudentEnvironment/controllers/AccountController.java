package com.example.StudentEnvironment.controllers;


import com.example.StudentEnvironment.entities.User;
import com.example.StudentEnvironment.entities.Place;
import com.example.StudentEnvironment.services.UserService;
import com.example.StudentEnvironment.services.PlaceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
/**
 * Класс представляет собой контроллер.
 * Обрабатывает запросы на предоставление страницы
 * личного кабинета пользователя и администратора.
 */
@Controller
@AllArgsConstructor
public class AccountController {
    @Autowired
    private PlaceService placeService;
    private UserService userService;
    /**
     * Метод получения страницы личного кабинета
     *  @param model объект Model, содержащий атрибуты для рендеринга представления
     *  @param session объект, необходимы для получения имени текущего пользователя
     *  @return имя представления для страницы "Личный кабинет"
     */
    @GetMapping("/account")
    @PreAuthorize("hasAnyAuthority('STUDENT', 'HEADMAN','ADMIN')")
    public String personalAccount(HttpServletRequest request, Model model, HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        session.setAttribute("username", username);
        if (username.equals("anonymousUser")) {
            username = "Вы не авторизованы";
        }
        model.addAttribute("username", username);
        model.addAttribute("requestURI", request.getRequestURI());
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));
        Long id = userService.getIDByName(username);
        if (isAdmin) {
            return "admin_page";
        } else {
            Iterable<Place> places = placeService.readAllByUserId(id);
            model.addAttribute("places", places);
            return "account_page";
        }
    }

    /**
     * Метод сохранения заказа администратором
     *  @param model объект Model, содержащий атрибуты для рендеринга представления
     *  @param str объект, содержащий набор продуктов
     *  @param username объект, имя текущего пользователя
     *  @param redirectAttributes объект, сохраняющий сообщение для пользователя при переходе
     *  @return имя представления для страницы "Личный кабинет"
     */
//    @PostMapping("/admin_save_place")
//    @PreAuthorize("hasAuthority('ADMIN')")
//    public String save_admin_place(@RequestParam("username") String username,
//                                   @RequestParam("str") String str,
//                                   Model model, RedirectAttributes redirectAttributes) {
//        String amount = placeService.getPlaceAmount(str);
//        Long user_id = userService.getIDByName(username);
//        str = str.replace("_", " ");
//        System.out.println(str);
//        System.out.println(amount);
//        System.out.println(user_id);
//        if (!amount.equals("-1") && !amount.equals("0") && user_id > 0) {
//            placeService.create(amount, str, user_id);
//            System.out.println("OK");
//            redirectAttributes.addFlashAttribute("mes", "Заказ успешно создан");
//            return "redirect:/account";
//        } else if (amount.equals("-1") && user_id < 0){
//            redirectAttributes.addFlashAttribute("er", "Ошибка в обоих полях");
//            return "redirect:/account";
//        } else if (amount.equals("-1")) {
//            redirectAttributes.addFlashAttribute("er", "Ошибка в наборе растений");
//            return "redirect:/account";
//        }
//        else {
//            redirectAttributes.addFlashAttribute("er", "Пользователь не существует");
//            return "redirect:/account";
//        }
//    }
    /**
     * Метод удаления заказа администратором
     *  @param model объект Model, содержащий атрибуты для рендеринга представления
     *  @param place_id объект, содержащий номер заказа
     *  @param redirectAttributes объект, сохраняющий сообщение для пользователя при переходе
     *  @return имя представления для страницы "Личный кабинет"
     */
    @PostMapping("/delete_place")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String delete_place(@RequestParam("id") Long place_id,
                               Model model, RedirectAttributes redirectAttributes) {

        if (placeService.exists(place_id)) {
            placeService.delete(place_id);
            redirectAttributes.addFlashAttribute("mes", "Заказ успешно удален");
            return "redirect:/account";
        } else {
            redirectAttributes.addFlashAttribute("er", "Такого заказа нет");
            return "redirect:/account";
        }
    }
    /**
     * Метод поиска всех пользователей
     *  @param model объект Model, содержащий атрибуты для рендеринга представления
     *  @param redirectAttributes объект, сохраняющий сообщение для пользователя при переходе
     *  @return имя представления для страницы "Личный кабинет"
     */
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String find_users(Model model, RedirectAttributes redirectAttributes) {
        Iterable<User> users = userService.readAll();
        redirectAttributes.addFlashAttribute("users", users);
        return "redirect:/account";
    }




    /**
     * Метод поиска заказа администратором
     *  @param place_id объект, содержащий номер заказа
     *  @param model объект Model, содержащий атрибуты для рендеринга представления
     *  @param redirectAttributes объект, сохраняющий сообщение для пользователя при переходе
     *  @return имя представления для страницы "Личный кабинет"
     */
    @GetMapping("/find_place_by_ID")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String find_place_by_ID(@RequestParam("id") Long place_id,
                                   Model model, RedirectAttributes redirectAttributes) {
        Place place  = placeService.getPlaceByID(place_id);
        if (place != null) {
            System.out.println("OK");
            redirectAttributes.addFlashAttribute("place", place);
            return "redirect:/account";
        } else {
            redirectAttributes.addFlashAttribute("er", "Такого заказа нет");
            return "redirect:/account";
        }
    }
    /**
     * Метод поиска всех заказов пользователя
     *  @param username объект, имя текущего пользователя
     *  @param model объект Model, содержащий атрибуты для рендеринга представления
     *  @param redirectAttributes объект, сохраняющий сообщение для пользователя при переходе
     *  @return имя представления для страницы "Личный кабинет"
     */
    @GetMapping("/find_user_places")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String find_user_places(@RequestParam("username") String username,
                                       Model model, RedirectAttributes redirectAttributes) {
        if (userService.exists(username) && !username.equals("anonymousUser")) {
            Long id = userService.getIDByName(username);
            Iterable<Place> places = placeService.readAllByUserId(id);
            redirectAttributes.addFlashAttribute("places", places);
            return "redirect:/account";
        }
        redirectAttributes.addFlashAttribute("er", "Такого пользователя нет");
        return "redirect:/account";
    }
}





