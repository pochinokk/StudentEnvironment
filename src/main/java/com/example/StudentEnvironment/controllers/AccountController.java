package com.example.StudentEnvironment.controllers;


import com.example.StudentEnvironment.entities.ExchangeRequest;
import com.example.StudentEnvironment.entities.Group;
import com.example.StudentEnvironment.entities.User;
import com.example.StudentEnvironment.entities.Place;
import com.example.StudentEnvironment.services.ExchangeService;
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

import java.util.List;

/**
 * Контроллер, управляющий действиями на странице личного кабинета.
 */
@Controller
@AllArgsConstructor
public class AccountController {
    @Autowired
    private PlaceService placeService;
    private UserService userService;
    private final ExchangeService exchangeService;
    /**
     * Обработка запроса на отображение страницы личного кабинета.
     *
     * @param request HTTP-запрос
     * @param model модель представления
     * @param session HTTP-сессия
     * @return имя представления: account_page или admin_page
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
        Long user_id = userService.getIDByName(username);
        User user = userService.findById(user_id);
        model.addAttribute("user", user);
        Group group = user.getGroup();
        model.addAttribute("group", group);
        model.addAttribute("requestURI", request.getRequestURI());
        // Входящие запросы — кто отправил тебе запрос на обмен
        User currentUser = userService.getCurrentUser();
        List<ExchangeRequest> incomingRequests = exchangeService.findRequestsToUser(currentUser);
        model.addAttribute("incomingRequests", incomingRequests);

        // Есть ли исходящий запрос (к кому ты отправил)
        boolean hasOutgoingRequest = currentUser.getExchange_user() != null;
        model.addAttribute("hasOutgoingRequest", hasOutgoingRequest);

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

//    /**
//     * Метод сохранения заказа администратором
//     *  @param model объект Model, содержащий атрибуты для рендеринга представления
//     *  @param str объект, содержащий набор продуктов
//     *  @param username объект, имя текущего пользователя
//     *  @param redirectAttributes объект, сохраняющий сообщение для пользователя при переходе
//     *  @return имя представления для страницы "Личный кабинет"
//     */
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

//    /**
//     * Поиск всех мест пользователя по имени.
//     *
//     * @param username имя пользователя
//     * @param model модель представления
//     * @param redirectAttributes атрибуты перенаправления
//     * @return редирект на страницу личного кабинета
//     */
//    @GetMapping("/find_user_places")
//    @PreAuthorize("hasAuthority('ADMIN')")
//    public String find_user_places(@RequestParam("username") String username,
//                                       Model model, RedirectAttributes redirectAttributes) {
//        if (userService.exists(username) && !username.equals("anonymousUser")) {
//            Long id = userService.getIDByName(username);
//            Iterable<Place> places = placeService.readAllByUserId(id);
//            redirectAttributes.addFlashAttribute("places", places);
//            return "redirect:/account";
//        }
//        redirectAttributes.addFlashAttribute("er", "Такого пользователя нет");
//        return "redirect:/account";
//    }


    @PostMapping("/exchange/send")
    public String sendExchange(@RequestParam Long toUserId) {
        User from = userService.getCurrentUser();
        User to = userService.findById(toUserId);
        exchangeService.sendExchangeRequest(from, to);
        return "redirect:/account";
    }

    @PostMapping("/exchange/cancel")
    public String cancelExchange() {
        User from = userService.getCurrentUser();
        exchangeService.cancelExchangeRequest(from);
        return "redirect:/account";
    }

    @PostMapping("/exchange/confirm")
    public String confirmExchange(@RequestParam Long requestId) {
        exchangeService.confirmExchangeRequest(requestId);
        return "redirect:/account";
    }
}





