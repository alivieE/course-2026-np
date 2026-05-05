package ua.com.kisit.course2026np.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.com.kisit.course2026np.entity.User;
import ua.com.kisit.course2026np.security.SecurityUserDetails;
import ua.com.kisit.course2026np.service.UserService;

@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private static final Logger securityLog = LoggerFactory.getLogger("SECURITY");

    public static final String SESSION_USER_ATTR = "loginUser";

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public ModelAndView loginPage(@RequestParam(value = "error", required = false) String error,
                                  @RequestParam(value = "logout", required = false) String logout) {
        ModelAndView mv = new ModelAndView("login");
        mv.addObject("title", "Вхід - SkyAirlines");
        if (error != null) {
            mv.addObject("error", "Невірний email або пароль. Спробуйте ще раз.");
        }
        if (logout != null) {
            mv.addObject("info", "Ви успішно вийшли з системи.");
        }
        return mv;
    }

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
        log.debug("Спроба реєстрації нового користувача: email={}", email);
        try {
            User user = userService.register(firstName, lastName, email, password);

            SecurityUserDetails principal = new SecurityUserDetails(user);
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    principal, null, principal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
            request.getSession().setAttribute(
                    "SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            securityLog.info("REGISTER_SUCCESS user={} role={} ip={}",
                    user.getEmail(), user.getRole(), request.getRemoteAddr());

            redirectAttrs.addFlashAttribute("toastType", "success");
            redirectAttrs.addFlashAttribute("toastMessage",
                    "Акаунт створено! Ласкаво просимо, " + user.getFirstName() + ".");

            return new ModelAndView("redirect:/");
        } catch (IllegalArgumentException ex) {
            securityLog.warn("REGISTER_FAILURE email={} ip={} reason={}",
                    email, request.getRemoteAddr(), ex.getMessage());
            ModelAndView mv = new ModelAndView("register");
            mv.addObject("title", "Реєстрація - SkyAirlines");
            mv.addObject("error", ex.getMessage());
            mv.addObject("firstName", firstName);
            mv.addObject("lastName", lastName);
            mv.addObject("email", email);
            return mv;
        }
    }
}
