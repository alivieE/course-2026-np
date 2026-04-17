package ua.com.kisit.course2026np.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.com.kisit.course2026np.entity.User;
import ua.com.kisit.course2026np.service.UserService;

import java.util.Optional;

/**
 * Контролер автентифікації користувачів через HttpSession.
 *
 * Після успішного входу об'єкт User зберігається у сесії під атрибутом "loginUser".
 * Також для показу спливаючих повідомлень (toast) використовуються flash-атрибути
 * RedirectAttributes: toastMessage і toastType ("success" / "info" / "danger"),
 * які переживають redirect і доступні шаблону лише один раз.
 */
@Controller
public class AuthController {

    public static final String SESSION_USER_ATTR = "loginUser";

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // ----- LOGIN -----

    @GetMapping("/login")
    public ModelAndView loginPage() {
        ModelAndView mv = new ModelAndView("login");
        mv.addObject("title", "Вхід - SkyAirlines");
        return mv;
    }

    @PostMapping("/login")
    public ModelAndView loginSubmit(@RequestParam("email") String email,
                                    @RequestParam("password") String password,
                                    HttpServletRequest request,
                                    RedirectAttributes redirectAttrs) {
        Optional<User> userOpt = userService.login(email, password);

        if (userOpt.isEmpty()) {
            ModelAndView mv = new ModelAndView("login");
            mv.addObject("title", "Вхід - SkyAirlines");
            mv.addObject("error", "Невірний email або пароль. Спробуйте ще раз.");
            mv.addObject("email", email);
            return mv;
        }

        HttpSession session = request.getSession();
        if (session.getAttribute(SESSION_USER_ATTR) == null) {
            session.setAttribute(SESSION_USER_ATTR, userOpt.get());
        }

        // Flash-повідомлення після redirect
        redirectAttrs.addFlashAttribute("toastType", "success");
        redirectAttrs.addFlashAttribute("toastMessage",
                "Вітаємо, " + userOpt.get().getFirstName() + "! Ви успішно увійшли в систему.");

        return new ModelAndView("redirect:/");
    }

    // ----- REGISTER -----

    @GetMapping("/register")
    public ModelAndView registerPage() {
        ModelAndView mv = new ModelAndView("register");
        mv.addObject("title", "Реєстрація - SkyAirlines");
        return mv;
    }

    @PostMapping("/register")
    public ModelAndView registerSubmit(@RequestParam("firstName") String firstName,
                                       @RequestParam("lastName") String lastName,
                                       @RequestParam("email") String email,
                                       @RequestParam("password") String password,
                                       HttpServletRequest request,
                                       RedirectAttributes redirectAttrs) {
        try {
            User user = userService.register(firstName, lastName, email, password);
            request.getSession().setAttribute(SESSION_USER_ATTR, user);

            redirectAttrs.addFlashAttribute("toastType", "success");
            redirectAttrs.addFlashAttribute("toastMessage",
                    "Акаунт створено! Ласкаво просимо, " + user.getFirstName() + ".");

            return new ModelAndView("redirect:/");
        } catch (IllegalArgumentException ex) {
            ModelAndView mv = new ModelAndView("register");
            mv.addObject("title", "Реєстрація - SkyAirlines");
            mv.addObject("error", ex.getMessage());
            mv.addObject("firstName", firstName);
            mv.addObject("lastName", lastName);
            mv.addObject("email", email);
            return mv;
        }
    }

    // ----- LOGOUT -----

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttrs) {
        // Забираємо ім'я ДО invalidate (після — сесія недоступна)
        Object attr = session.getAttribute(SESSION_USER_ATTR);
        String firstName = attr instanceof User ? ((User) attr).getFirstName() : null;

        session.invalidate();

        redirectAttrs.addFlashAttribute("toastType", "info");
        redirectAttrs.addFlashAttribute("toastMessage",
                firstName != null
                        ? "До побачення, " + firstName + "! Ви вийшли з системи."
                        : "Ви вийшли з системи.");

        return "redirect:/";
    }
}