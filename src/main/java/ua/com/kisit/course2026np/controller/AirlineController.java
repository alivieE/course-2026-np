package ua.com.kisit.course2026np.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AirlineController {

    // Головна сторінка авіакомпанії
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Головна - SkyAirlines");
        return "index";
    }

    // Сторінка з доступними рейсами
    @GetMapping("/flights")
    public String flights(Model model) {
        model.addAttribute("title", "Розклад рейсів");
        return "flights";
    }

    // Сторінка бронювання квитка
    @GetMapping("/booking")
    public String booking(Model model) {
        model.addAttribute("title", "Бронювання квитків");
        return "booking";
    }
}