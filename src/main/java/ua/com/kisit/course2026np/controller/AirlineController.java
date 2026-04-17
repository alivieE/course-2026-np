package ua.com.kisit.course2026np.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import ua.com.kisit.course2026np.entity.Flight;
import ua.com.kisit.course2026np.entity.FlightStatus;
import ua.com.kisit.course2026np.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AirlineController {

    private List<Flight> getTestFlights() {
        List<Flight> flights = new ArrayList<>();

        Flight f1 = new Flight();
        f1.setId(1L);
        f1.setFlightNumber("SA-101");
        f1.setDepartureCity("Київ");
        f1.setArrivalCity("Париж");
        f1.setDepartureTime(LocalDateTime.of(2026, 11, 15, 10, 30));
        f1.setArrivalTime(LocalDateTime.of(2026, 11, 15, 13, 45));
        f1.setStatus(FlightStatus.PLANNED);

        Flight f2 = new Flight();
        f2.setId(2L);
        f2.setFlightNumber("SA-205");
        f2.setDepartureCity("Львів");
        f2.setArrivalCity("Лондон");
        f2.setDepartureTime(LocalDateTime.of(2026, 11, 16, 14, 15));
        f2.setArrivalTime(LocalDateTime.of(2026, 11, 16, 16, 0));
        f2.setStatus(FlightStatus.PLANNED);

        Flight f3 = new Flight();
        f3.setId(3L);
        f3.setFlightNumber("SA-312");
        f3.setDepartureCity("Київ");
        f3.setArrivalCity("Рим");
        f3.setDepartureTime(LocalDateTime.of(2026, 11, 18, 9, 0));
        f3.setArrivalTime(LocalDateTime.of(2026, 11, 18, 11, 30));
        f3.setStatus(FlightStatus.CANCELLED);

        flights.add(f1);
        flights.add(f2);
        flights.add(f3);
        return flights;
    }

    /**
     * Повертає User з сесії або null.
     * Викликається з кожної сторінки для відображення стану авторизації.
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

    @GetMapping("/flights")
    public ModelAndView flights(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("flights");
        List<Flight> flightList = getTestFlights();
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