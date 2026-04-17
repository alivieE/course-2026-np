package ua.com.kisit.course2026np.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ua.com.kisit.course2026np.entity.User;
import ua.com.kisit.course2026np.service.UserService;

import java.util.Optional;

/**
 * Контролер автентифікації користувачів через HttpSession.
 *
 * Ключова ідея: після успішного входу об'єкт User зберігається
 * у сесії під атрибутом "loginUser". На всіх наступних сторінках
 * вміст сесії доступний через request.getSession().getAttribute("loginUser"),
 * що дозволяє визначити, чи автентифікований користувач, і показувати
 * відповідний інтерфейс (ім'я користувача та кнопку «Вийти» замість
 * кнопок «Увійти / Зареєструватися»).
 */
@Controller
public class AuthController {

    /** Ім'я атрибута сесії, під яким зберігається User залогіненого користувача. */
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
                                    HttpServletRequest request) {
        Optional<User> userOpt = userService.login(email, password);

        if (userOpt.isEmpty()) {
            // Невірні облікові дані — повертаємося на сторінку з повідомленням
            ModelAndView mv = new ModelAndView("login");
            mv.addObject("title", "Вхід - SkyAirlines");
            mv.addObject("error", "Невірний email або пароль. Спробуйте ще раз.");
            mv.addObject("email", email);
            return mv;
        }

        // Успіх — записуємо користувача в сесію
        HttpSession session = request.getSession();
        if (session.getAttribute(SESSION_USER_ATTR) == null) {
            session.setAttribute(SESSION_USER_ATTR, userOpt.get());
        }
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
                                       HttpServletRequest request) {
        try {
            User user = userService.register(firstName, lastName, email, password);
            // Одразу логінимо користувача після успішної реєстрації
            request.getSession().setAttribute(SESSION_USER_ATTR, user);
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
    public String logout(HttpSession session) {
        // Повне знищення сесії — після цього getAttribute("loginUser") буде null
        session.invalidate();
        return "redirect:/";
    }
}