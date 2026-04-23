package ua.com.kisit.course2026np.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import ua.com.kisit.course2026np.entity.Flight;
import ua.com.kisit.course2026np.entity.User;
import ua.com.kisit.course2026np.service.FlightService;

import java.util.List;

@Controller
public class AirlineController {

    private final FlightService flightService;

    public AirlineController(FlightService flightService) {
        this.flightService = flightService;
    }

    /**
     * Повертає User з сесії або null.
     */
    private User currentUser(HttpSession session) {
        Object attr = session.getAttribute(AuthController.SESSION_USER_ATTR);
        return attr instanceof User ? (User) attr : null;
    }

    @GetMapping("/")
    public ModelAndView home(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("title", "Головна - SkyAirlines");
        modelAndView.addObject("welcomeMessage", "Ласкаво просимо до SkyAirlines!");
        modelAndView.addObject("currentUser", currentUser(session));
        return modelAndView;
    }

    /**
     * Розклад рейсів для клієнта — дані беруться з БД через FlightService.
     */
    @GetMapping("/flights")
    public ModelAndView flights(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("flights");
        List<Flight> flightList = flightService.getAllFlights();
        modelAndView.addObject("title", "Розклад рейсів - SkyAirlines");
        modelAndView.addObject("flights", flightList);
        modelAndView.addObject("flightsCount", flightList.size());
        modelAndView.addObject("currentUser", currentUser(session));
        return modelAndView;
    }

    /**
     * Сторінка оформлення квитка — ЗАХИЩЕНА.
     * Якщо в сесії немає loginUser — перенаправлення на /login.
     */
    @GetMapping("/booking")
    public ModelAndView booking(HttpSession session) {
        User user = currentUser(session);
        if (user == null) {
            return new ModelAndView("redirect:/login");
        }
        ModelAndView modelAndView = new ModelAndView("booking");
        modelAndView.addObject("title", "Оформлення квитка - SkyAirlines");
        modelAndView.addObject("currentUser", user);
        return modelAndView;
    }
}