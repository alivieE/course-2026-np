package ua.com.kisit.course2026np.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.com.kisit.course2026np.entity.Flight;
import ua.com.kisit.course2026np.entity.FlightStatus;
import ua.com.kisit.course2026np.entity.User;
import ua.com.kisit.course2026np.entity.UserRole;
import ua.com.kisit.course2026np.service.FlightService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Контролер адміністративної частини додатку (Лаб 11).
 *
 * Відповідає за CRUD-операції над рейсами, доступні лише користувачам з роллю ADMIN.
 * Доступ до маршрутів /admin/** контролюється методом requireAdmin(), який перевіряє
 * наявність у HttpSession атрибута loginUser з роллю UserRole.ADMIN.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final DateTimeFormatter INPUT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private final FlightService flightService;

    public AdminController(FlightService flightService) {
        this.flightService = flightService;
    }

    /**
     * Перевірка прав адміністратора. Повертає User якщо доступ дозволено,
     * або null — якщо немає прав (у такому випадку викликачу слід виконати redirect).
     */
    private User requireAdmin(HttpSession session) {
        Object attr = session.getAttribute(AuthController.SESSION_USER_ATTR);
        if (!(attr instanceof User)) return null;
        User user = (User) attr;
        return user.getRole() == UserRole.ADMIN ? user : null;
    }

    // ===== СПИСОК РЕЙСІВ =====

    @GetMapping("/flights")
    public ModelAndView listFlights(HttpSession session, RedirectAttributes ra) {
        User admin = requireAdmin(session);
        if (admin == null) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Доступ заборонено. Необхідні права адміністратора.");
            return new ModelAndView("redirect:/login");
        }

        ModelAndView mv = new ModelAndView("admin/flights");
        mv.addObject("title", "Адмінпанель — Рейси");
        mv.addObject("currentUser", admin);
        mv.addObject("flights", flightService.getAllFlights());
        return mv;
    }

    // ===== ФОРМА НОВОГО РЕЙСУ =====

    @GetMapping("/flights/new")
    public ModelAndView newFlightForm(HttpSession session) {
        User admin = requireAdmin(session);
        if (admin == null) return new ModelAndView("redirect:/login");

        ModelAndView mv = new ModelAndView("admin/flight-form");
        mv.addObject("title", "Створення рейсу — Адмінпанель");
        mv.addObject("currentUser", admin);
        mv.addObject("flight", new Flight());
        mv.addObject("isNew", true);
        mv.addObject("statuses", FlightStatus.values());
        return mv;
    }

    // ===== СТВОРЕННЯ РЕЙСУ =====

    @PostMapping("/flights")
    public ModelAndView createFlight(@RequestParam String flightNumber,
                                     @RequestParam String departureCity,
                                     @RequestParam String arrivalCity,
                                     @RequestParam String departureTime,
                                     @RequestParam String arrivalTime,
                                     @RequestParam FlightStatus status,
                                     HttpSession session,
                                     RedirectAttributes ra) {
        User admin = requireAdmin(session);
        if (admin == null) return new ModelAndView("redirect:/login");

        try {
            Flight flight = Flight.builder()
                    .flightNumber(flightNumber)
                    .departureCity(departureCity)
                    .arrivalCity(arrivalCity)
                    .departureTime(LocalDateTime.parse(departureTime, INPUT_FORMATTER))
                    .arrivalTime(LocalDateTime.parse(arrivalTime, INPUT_FORMATTER))
                    .status(status)
                    .user(admin) // автор рейсу — поточний адмін
                    .build();

            flightService.create(flight);

            ra.addFlashAttribute("toastType", "success");
            ra.addFlashAttribute("toastMessage", "Рейс " + flightNumber + " успішно створено.");
            return new ModelAndView("redirect:/admin/flights");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", ex.getMessage());
            return new ModelAndView("redirect:/admin/flights/new");
        }
    }

    // ===== ФОРМА РЕДАГУВАННЯ =====

    @GetMapping("/flights/{id}/edit")
    public ModelAndView editFlightForm(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User admin = requireAdmin(session);
        if (admin == null) return new ModelAndView("redirect:/login");

        Optional<Flight> flightOpt = flightService.getById(id);
        if (flightOpt.isEmpty()) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Рейс з ID " + id + " не знайдено.");
            return new ModelAndView("redirect:/admin/flights");
        }

        ModelAndView mv = new ModelAndView("admin/flight-form");
        mv.addObject("title", "Редагування рейсу — Адмінпанель");
        mv.addObject("currentUser", admin);
        mv.addObject("flight", flightOpt.get());
        mv.addObject("isNew", false);
        mv.addObject("statuses", FlightStatus.values());
        return mv;
    }

    // ===== ОНОВЛЕННЯ РЕЙСУ =====

    @PostMapping("/flights/{id}")
    public ModelAndView updateFlight(@PathVariable Long id,
                                     @RequestParam String flightNumber,
                                     @RequestParam String departureCity,
                                     @RequestParam String arrivalCity,
                                     @RequestParam String departureTime,
                                     @RequestParam String arrivalTime,
                                     @RequestParam FlightStatus status,
                                     HttpSession session,
                                     RedirectAttributes ra) {
        User admin = requireAdmin(session);
        if (admin == null) return new ModelAndView("redirect:/login");

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

            ra.addFlashAttribute("toastType", "success");
            ra.addFlashAttribute("toastMessage", "Рейс " + flightNumber + " оновлено.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Помилка оновлення: " + ex.getMessage());
        }
        return new ModelAndView("redirect:/admin/flights");
    }

    // ===== ВИДАЛЕННЯ РЕЙСУ =====

    @PostMapping("/flights/{id}/delete")
    public ModelAndView deleteFlight(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User admin = requireAdmin(session);
        if (admin == null) return new ModelAndView("redirect:/login");

        try {
            flightService.delete(id);
            ra.addFlashAttribute("toastType", "info");
            ra.addFlashAttribute("toastMessage", "Рейс видалено.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Помилка видалення: " + ex.getMessage());
        }
        return new ModelAndView("redirect:/admin/flights");
    }
}
