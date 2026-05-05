package ua.com.kisit.course2026np.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.com.kisit.course2026np.entity.Flight;
import ua.com.kisit.course2026np.entity.FlightStatus;
import ua.com.kisit.course2026np.entity.User;
import ua.com.kisit.course2026np.security.SecurityUserDetails;
import ua.com.kisit.course2026np.service.FlightService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
@RequestMapping("/admin/flights")
public class FlightManagerController {

    private static final Logger log = LoggerFactory.getLogger(FlightManagerController.class);
    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT");

    private static final DateTimeFormatter INPUT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private final FlightService flightService;

    public FlightManagerController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping
    public ModelAndView listFlights(@AuthenticationPrincipal SecurityUserDetails principal) {
        log.debug("Перегляд списку рейсів користувачем {}", principal.getUsername());
        ModelAndView mv = new ModelAndView("admin/flights");
        mv.addObject("title", "Адмінпанель — Рейси");
        mv.addObject("currentUser", principal.getUser());
        mv.addObject("flights", flightService.getAllFlights());
        return mv;
    }

    @GetMapping("/new")
    public ModelAndView newFlightForm(@AuthenticationPrincipal SecurityUserDetails principal) {
        ModelAndView mv = new ModelAndView("admin/flight-form");
        mv.addObject("title", "Створення рейсу — Адмінпанель");
        mv.addObject("currentUser", principal.getUser());
        mv.addObject("flight", new Flight());
        mv.addObject("isNew", true);
        mv.addObject("statuses", FlightStatus.values());
        return mv;
    }

    @PostMapping
    public ModelAndView createFlight(@RequestParam String flightNumber,
                                     @RequestParam String departureCity,
                                     @RequestParam String arrivalCity,
                                     @RequestParam String departureTime,
                                     @RequestParam String arrivalTime,
                                     @RequestParam FlightStatus status,
                                     @AuthenticationPrincipal SecurityUserDetails principal,
                                     RedirectAttributes ra) {
        User admin = principal.getUser();
        try {
            Flight flight = Flight.builder()
                    .flightNumber(flightNumber)
                    .departureCity(departureCity)
                    .arrivalCity(arrivalCity)
                    .departureTime(LocalDateTime.parse(departureTime, INPUT_FORMATTER))
                    .arrivalTime(LocalDateTime.parse(arrivalTime, INPUT_FORMATTER))
                    .status(status)
                    .user(admin)
                    .build();

            Flight created = flightService.create(flight);
            auditLog.info("FLIGHT_CREATE actor={} flightId={} flightNumber={} from={} to={}",
                    principal.getUsername(), created.getId(), flightNumber, departureCity, arrivalCity);

            ra.addFlashAttribute("toastType", "success");
            ra.addFlashAttribute("toastMessage", "Рейс " + flightNumber + " успішно створено.");
            return new ModelAndView("redirect:/admin/flights");
        } catch (IllegalArgumentException ex) {
            log.warn("Не вдалось створити рейс {}: {}", flightNumber, ex.getMessage());
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", ex.getMessage());
            return new ModelAndView("redirect:/admin/flights/new");
        }
    }

    @GetMapping("/{id}/edit")
    public ModelAndView editFlightForm(@PathVariable Long id,
                                       @AuthenticationPrincipal SecurityUserDetails principal,
                                       RedirectAttributes ra) {
        Optional<Flight> flightOpt = flightService.getById(id);
        if (flightOpt.isEmpty()) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Рейс з ID " + id + " не знайдено.");
            return new ModelAndView("redirect:/admin/flights");
        }
        ModelAndView mv = new ModelAndView("admin/flight-form");
        mv.addObject("title", "Редагування рейсу — Адмінпанель");
        mv.addObject("currentUser", principal.getUser());
        mv.addObject("flight", flightOpt.get());
        mv.addObject("isNew", false);
        mv.addObject("statuses", FlightStatus.values());
        return mv;
    }

    @PostMapping("/{id}")
    public ModelAndView updateFlight(@PathVariable Long id,
                                     @RequestParam String flightNumber,
                                     @RequestParam String departureCity,
                                     @RequestParam String arrivalCity,
                                     @RequestParam String departureTime,
                                     @RequestParam String arrivalTime,
                                     @RequestParam FlightStatus status,
                                     @AuthenticationPrincipal SecurityUserDetails principal,
                                     RedirectAttributes ra) {
        try {
            Flight updated = Flight.builder()
                    .flightNumber(flightNumber)
                    .departureCity(departureCity)
                    .arrivalCity(arrivalCity)
                    .departureTime(LocalDateTime.parse(departureTime, INPUT_FORMATTER))
                    .arrivalTime(LocalDateTime.parse(arrivalTime, INPUT_FORMATTER))
                    .status(status)
                    .build();
            flightService.update(id, updated);
            auditLog.info("FLIGHT_UPDATE actor={} flightId={} flightNumber={} status={}",
                    principal.getUsername(), id, flightNumber, status);
            ra.addFlashAttribute("toastType", "success");
            ra.addFlashAttribute("toastMessage", "Рейс " + flightNumber + " оновлено.");
        } catch (RuntimeException ex) {
            log.warn("Помилка оновлення рейсу id={}: {}", id, ex.getMessage());
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Помилка оновлення: " + ex.getMessage());
        }
        return new ModelAndView("redirect:/admin/flights");
    }

    @PostMapping("/{id}/delete")
    public ModelAndView deleteFlight(@PathVariable Long id,
                                     @AuthenticationPrincipal SecurityUserDetails principal,
                                     RedirectAttributes ra) {
        try {
            flightService.delete(id);
            auditLog.info("FLIGHT_DELETE actor={} flightId={}", principal.getUsername(), id);
            ra.addFlashAttribute("toastType", "info");
            ra.addFlashAttribute("toastMessage", "Рейс видалено.");
        } catch (RuntimeException ex) {
            log.warn("Помилка видалення рейсу id={}: {}", id, ex.getMessage());
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Помилка видалення: " + ex.getMessage());
        }
        return new ModelAndView("redirect:/admin/flights");
    }
}
