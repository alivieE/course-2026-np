package ua.com.kisit.course2026np.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.com.kisit.course2026np.entity.Passenger;
import ua.com.kisit.course2026np.security.SecurityUserDetails;
import ua.com.kisit.course2026np.service.PassengerService;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/admin/passengers")
public class PassengerManagerController {

    private static final Logger log = LoggerFactory.getLogger(PassengerManagerController.class);
    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT");

    private final PassengerService passengerService;

    public PassengerManagerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @GetMapping
    public ModelAndView listPassengers(@RequestParam(required = false) String search,
                                       @AuthenticationPrincipal SecurityUserDetails principal) {
        log.debug("Перегляд списку пасажирів користувачем {}, search={}",
                principal.getUsername(), search);
        ModelAndView mv = new ModelAndView("admin/passengers");
        mv.addObject("title", "Адмінпанель — Пасажири");
        mv.addObject("currentUser", principal.getUser());

        if (search != null && !search.isBlank()) {
            mv.addObject("passengers", passengerService.searchByLastName(search));
            mv.addObject("searchQuery", search);
        } else {
            mv.addObject("passengers", passengerService.getAll());
        }
        return mv;
    }

    @GetMapping("/new")
    public ModelAndView newPassengerForm(@AuthenticationPrincipal SecurityUserDetails principal) {
        ModelAndView mv = new ModelAndView("admin/passenger-form");
        mv.addObject("title", "Створення пасажира — Адмінпанель");
        mv.addObject("currentUser", principal.getUser());
        mv.addObject("passenger", new Passenger());
        mv.addObject("isNew", true);
        mv.addObject("genders", Passenger.Gender.values());
        return mv;
    }

    @PostMapping
    public ModelAndView createPassenger(@RequestParam String firstName,
                                        @RequestParam String lastName,
                                        @RequestParam String passportNumber,
                                        @RequestParam String email,
                                        @RequestParam(required = false) String phone,
                                        @RequestParam(required = false) String dateOfBirth,
                                        @RequestParam(required = false) Passenger.Gender gender,
                                        @AuthenticationPrincipal SecurityUserDetails principal,
                                        RedirectAttributes ra) {
        try {
            Passenger passenger = Passenger.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .passportNumber(passportNumber)
                    .email(email)
                    .phone(phone)
                    .dateOfBirth(dateOfBirth != null && !dateOfBirth.isBlank()
                            ? LocalDate.parse(dateOfBirth) : null)
                    .gender(gender)
                    .build();
            Passenger created = passengerService.create(passenger);
            auditLog.info("PASSENGER_CREATE actor={} passengerId={} email={} fullName=\"{} {}\"",
                    principal.getUsername(), created.getId(), email, lastName, firstName);
            ra.addFlashAttribute("toastType", "success");
            ra.addFlashAttribute("toastMessage",
                    "Пасажира " + lastName + " " + firstName + " створено.");
            return new ModelAndView("redirect:/admin/passengers");
        } catch (IllegalArgumentException ex) {
            log.warn("Не вдалось створити пасажира {}: {}", email, ex.getMessage());
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", ex.getMessage());
            return new ModelAndView("redirect:/admin/passengers/new");
        }
    }

    @GetMapping("/{id}/edit")
    public ModelAndView editPassengerForm(@PathVariable Long id,
                                          @AuthenticationPrincipal SecurityUserDetails principal,
                                          RedirectAttributes ra) {
        Optional<Passenger> pOpt = passengerService.getById(id);
        if (pOpt.isEmpty()) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Пасажира з ID " + id + " не знайдено.");
            return new ModelAndView("redirect:/admin/passengers");
        }
        ModelAndView mv = new ModelAndView("admin/passenger-form");
        mv.addObject("title", "Редагування пасажира — Адмінпанель");
        mv.addObject("currentUser", principal.getUser());
        mv.addObject("passenger", pOpt.get());
        mv.addObject("isNew", false);
        mv.addObject("genders", Passenger.Gender.values());
        return mv;
    }

    @PostMapping("/{id}")
    public ModelAndView updatePassenger(@PathVariable Long id,
                                        @RequestParam String firstName,
                                        @RequestParam String lastName,
                                        @RequestParam String passportNumber,
                                        @RequestParam String email,
                                        @RequestParam(required = false) String phone,
                                        @RequestParam(required = false) String dateOfBirth,
                                        @RequestParam(required = false) Passenger.Gender gender,
                                        @AuthenticationPrincipal SecurityUserDetails principal,
                                        RedirectAttributes ra) {
        try {
            Passenger updated = Passenger.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .passportNumber(passportNumber)
                    .email(email)
                    .phone(phone)
                    .dateOfBirth(dateOfBirth != null && !dateOfBirth.isBlank()
                            ? LocalDate.parse(dateOfBirth) : null)
                    .gender(gender)
                    .build();
            passengerService.update(id, updated);
            auditLog.info("PASSENGER_UPDATE actor={} passengerId={} email={}",
                    principal.getUsername(), id, email);
            ra.addFlashAttribute("toastType", "success");
            ra.addFlashAttribute("toastMessage", "Пасажира оновлено.");
        } catch (RuntimeException ex) {
            log.warn("Помилка оновлення пасажира id={}: {}", id, ex.getMessage());
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Помилка оновлення: " + ex.getMessage());
        }
        return new ModelAndView("redirect:/admin/passengers");
    }

    @PostMapping("/{id}/delete")
    public ModelAndView deletePassenger(@PathVariable Long id,
                                        @AuthenticationPrincipal SecurityUserDetails principal,
                                        RedirectAttributes ra) {
        try {
            passengerService.delete(id);
            auditLog.info("PASSENGER_DELETE actor={} passengerId={}", principal.getUsername(), id);
            ra.addFlashAttribute("toastType", "info");
            ra.addFlashAttribute("toastMessage", "Пасажира видалено.");
        } catch (RuntimeException ex) {
            log.warn("Помилка видалення пасажира id={}: {}", id, ex.getMessage());
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Помилка видалення: " + ex.getMessage());
        }
        return new ModelAndView("redirect:/admin/passengers");
    }
}
