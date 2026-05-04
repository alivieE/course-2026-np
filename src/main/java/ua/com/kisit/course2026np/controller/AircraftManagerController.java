package ua.com.kisit.course2026np.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.com.kisit.course2026np.entity.Aircraft;
import ua.com.kisit.course2026np.entity.User;
import ua.com.kisit.course2026np.entity.UserRole;
import ua.com.kisit.course2026np.service.AircraftService;

import java.util.Optional;

@Controller
@RequestMapping("/admin/aircrafts")
public class AircraftManagerController {

    private final AircraftService aircraftService;

    public AircraftManagerController(AircraftService aircraftService) {
        this.aircraftService = aircraftService;
    }

    private User requireAdmin(HttpSession session) {
        Object attr = session.getAttribute(AuthController.SESSION_USER_ATTR);
        if (!(attr instanceof User)) return null;
        User user = (User) attr;
        return user.getRole() == UserRole.ADMIN ? user : null;
    }

    @GetMapping
    public ModelAndView listAircrafts(HttpSession session) {
        User admin = requireAdmin(session);
        if (admin == null) return new ModelAndView("redirect:/login");

        ModelAndView mv = new ModelAndView("admin/aircrafts");
        mv.addObject("title", "Адмінпанель — Літаки");
        mv.addObject("currentUser", admin);
        mv.addObject("aircrafts", aircraftService.getAll());
        return mv;
    }

    @GetMapping("/new")
    public ModelAndView newAircraftForm(HttpSession session) {
        User admin = requireAdmin(session);
        if (admin == null) return new ModelAndView("redirect:/login");

        ModelAndView mv = new ModelAndView("admin/aircraft-form");
        mv.addObject("title", "Створення літака — Адмінпанель");
        mv.addObject("currentUser", admin);
        mv.addObject("aircraft", new Aircraft());
        mv.addObject("isNew", true);
        mv.addObject("statuses", Aircraft.AircraftStatus.values());
        return mv;
    }

    @PostMapping
    public ModelAndView createAircraft(@RequestParam String registrationNumber,
                                       @RequestParam String model,
                                       @RequestParam String manufacturer,
                                       @RequestParam(required = false) Integer yearOfManufacture,
                                       @RequestParam Integer totalSeats,
                                       @RequestParam(required = false) Integer maxRangeKm,
                                       @RequestParam Aircraft.AircraftStatus status,
                                       HttpSession session,
                                       RedirectAttributes ra) {
        if (requireAdmin(session) == null) return new ModelAndView("redirect:/login");
        try {
            Aircraft aircraft = Aircraft.builder()
                    .registrationNumber(registrationNumber)
                    .model(model)
                    .manufacturer(manufacturer)
                    .yearOfManufacture(yearOfManufacture)
                    .totalSeats(totalSeats)
                    .maxRangeKm(maxRangeKm)
                    .status(status)
                    .build();
            aircraftService.create(aircraft);
            ra.addFlashAttribute("toastType", "success");
            ra.addFlashAttribute("toastMessage", "Літак " + registrationNumber + " успішно створено.");
            return new ModelAndView("redirect:/admin/aircrafts");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", ex.getMessage());
            return new ModelAndView("redirect:/admin/aircrafts/new");
        }
    }

    @GetMapping("/{id}/edit")
    public ModelAndView editAircraftForm(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User admin = requireAdmin(session);
        if (admin == null) return new ModelAndView("redirect:/login");

        Optional<Aircraft> aOpt = aircraftService.getById(id);
        if (aOpt.isEmpty()) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Літак з ID " + id + " не знайдено.");
            return new ModelAndView("redirect:/admin/aircrafts");
        }
        ModelAndView mv = new ModelAndView("admin/aircraft-form");
        mv.addObject("title", "Редагування літака — Адмінпанель");
        mv.addObject("currentUser", admin);
        mv.addObject("aircraft", aOpt.get());
        mv.addObject("isNew", false);
        mv.addObject("statuses", Aircraft.AircraftStatus.values());
        return mv;
    }

    @PostMapping("/{id}")
    public ModelAndView updateAircraft(@PathVariable Long id,
                                       @RequestParam String registrationNumber,
                                       @RequestParam String model,
                                       @RequestParam String manufacturer,
                                       @RequestParam(required = false) Integer yearOfManufacture,
                                       @RequestParam Integer totalSeats,
                                       @RequestParam(required = false) Integer maxRangeKm,
                                       @RequestParam Aircraft.AircraftStatus status,
                                       HttpSession session,
                                       RedirectAttributes ra) {
        if (requireAdmin(session) == null) return new ModelAndView("redirect:/login");
        try {
            Aircraft updated = Aircraft.builder()
                    .registrationNumber(registrationNumber)
                    .model(model)
                    .manufacturer(manufacturer)
                    .yearOfManufacture(yearOfManufacture)
                    .totalSeats(totalSeats)
                    .maxRangeKm(maxRangeKm)
                    .status(status)
                    .build();
            aircraftService.update(id, updated);
            ra.addFlashAttribute("toastType", "success");
            ra.addFlashAttribute("toastMessage", "Літак " + registrationNumber + " оновлено.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Помилка оновлення: " + ex.getMessage());
        }
        return new ModelAndView("redirect:/admin/aircrafts");
    }

    @PostMapping("/{id}/delete")
    public ModelAndView deleteAircraft(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (requireAdmin(session) == null) return new ModelAndView("redirect:/login");
        try {
            aircraftService.delete(id);
            ra.addFlashAttribute("toastType", "info");
            ra.addFlashAttribute("toastMessage", "Літак видалено.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Помилка видалення: " + ex.getMessage());
        }
        return new ModelAndView("redirect:/admin/aircrafts");
    }

    // ==================== Бізнес-операції ====================

    @PostMapping("/{id}/maintenance")
    public ModelAndView sendToMaintenance(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (requireAdmin(session) == null) return new ModelAndView("redirect:/login");
        try {
            Aircraft aircraft = aircraftService.sendToMaintenance(id);
            ra.addFlashAttribute("toastType", "info");
            ra.addFlashAttribute("toastMessage",
                    "Літак " + aircraft.getRegistrationNumber() + " відправлено на технічне обслуговування.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Помилка: " + ex.getMessage());
        }
        return new ModelAndView("redirect:/admin/aircrafts");
    }

    @PostMapping("/{id}/activate")
    public ModelAndView activate(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (requireAdmin(session) == null) return new ModelAndView("redirect:/login");
        try {
            Aircraft aircraft = aircraftService.activate(id);
            ra.addFlashAttribute("toastType", "success");
            ra.addFlashAttribute("toastMessage",
                    "Літак " + aircraft.getRegistrationNumber() + " повернуто в експлуатацію.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Помилка: " + ex.getMessage());
        }
        return new ModelAndView("redirect:/admin/aircrafts");
    }

    @PostMapping("/{id}/retire")
    public ModelAndView retire(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (requireAdmin(session) == null) return new ModelAndView("redirect:/login");
        try {
            Aircraft aircraft = aircraftService.retire(id);
            ra.addFlashAttribute("toastType", "info");
            ra.addFlashAttribute("toastMessage",
                    "Літак " + aircraft.getRegistrationNumber() + " списано з експлуатації.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Помилка: " + ex.getMessage());
        }
        return new ModelAndView("redirect:/admin/aircrafts");
    }

    @GetMapping("/maintenance-needed")
    public ModelAndView listMaintenanceNeeded(HttpSession session) {
        User admin = requireAdmin(session);
        if (admin == null) return new ModelAndView("redirect:/login");

        ModelAndView mv = new ModelAndView("admin/aircrafts");
        mv.addObject("title", "Літаки, що потребують ТО");
        mv.addObject("currentUser", admin);
        mv.addObject("aircrafts", aircraftService.getAircraftsNeedingMaintenance());
        mv.addObject("filterTitle", "Літаки, що потребують технічного обслуговування");
        return mv;
    }
}
