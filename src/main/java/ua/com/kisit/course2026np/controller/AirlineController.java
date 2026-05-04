package ua.com.kisit.course2026np.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import ua.com.kisit.course2026np.entity.Flight;
import ua.com.kisit.course2026np.entity.User;
import ua.com.kisit.course2026np.security.SecurityUserDetails;
import ua.com.kisit.course2026np.service.FlightService;

import java.util.List;

@Controller
public class AirlineController {

    private final FlightService flightService;

    public AirlineController(FlightService flightService) {
        this.flightService = flightService;
    }

    private User extractUser(SecurityUserDetails principal) {
        return principal != null ? principal.getUser() : null;
    }

    @GetMapping("/")
    public ModelAndView home(@AuthenticationPrincipal SecurityUserDetails principal) {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("title", "Головна - SkyAirlines");
        modelAndView.addObject("welcomeMessage", "Ласкаво просимо до SkyAirlines!");
        modelAndView.addObject("currentUser", extractUser(principal));
        return modelAndView;
    }

    @GetMapping("/flights")
    public ModelAndView flights(@AuthenticationPrincipal SecurityUserDetails principal) {
        ModelAndView modelAndView = new ModelAndView("flights");
        List<Flight> flightList = flightService.getAllFlights();
        modelAndView.addObject("title", "Розклад рейсів - SkyAirlines");
        modelAndView.addObject("flights", flightList);
        modelAndView.addObject("flightsCount", flightList.size());
        modelAndView.addObject("currentUser", extractUser(principal));
        return modelAndView;
    }

    @GetMapping("/booking")
    public ModelAndView booking(@AuthenticationPrincipal SecurityUserDetails principal) {
        ModelAndView modelAndView = new ModelAndView("booking");
        modelAndView.addObject("title", "Оформлення квитка - SkyAirlines");
        modelAndView.addObject("currentUser", principal.getUser());
        return modelAndView;
    }
}