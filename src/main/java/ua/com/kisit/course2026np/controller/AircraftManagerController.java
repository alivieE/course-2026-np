package ua.com.kisit.course2026np.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.com.kisit.course2026np.entity.Aircraft;
import ua.com.kisit.course2026np.security.SecurityUserDetails;
import ua.com.kisit.course2026np.service.AircraftService;

import java.util.Optional;

@Controller
@RequestMapping("/admin/aircrafts")
public class AircraftManagerController {

    private final AircraftService aircraftService;

    public AircraftManagerController(AircraftService aircraftService) {
        this.aircraftService = aircraftService;
    }

    @GetMapping
    public ModelAndView listAircrafts(@AuthenticationPrincipal SecurityUserDetails principal) {
        ModelAndView mv = new ModelAndView("admin/aircrafts");
        mv.addObject("title", "Адмінпанель — Літаки");
        mv.addObject("currentUser", principal.getUser());
        mv.addObject("aircrafts", aircraftService.getAll());
        return mv;
    }

    @GetMapping("/new")
    public ModelAndView newAircraftForm(@AuthenticationPrincipal SecurityUserDetails principal) {
        ModelAndView mv = new ModelAndView("admin/aircraft-form");
        mv.addObject("title", "Створення літака — Адмінпанель");
        mv.addObject("currentUser", principal.getUser());
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
                                       RedirectAttributes ra) {
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
    public ModelAndView editAircraftForm(@PathVariable Long id,
                                         @AuthenticationPrincipal SecurityUserDetails principal,
                                         RedirectAttributes ra) {
        Optional<Aircraft> aOpt = aircraftService.getById(id);
        if (aOpt.isEmpty()) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Літак з ID " + id + " не знайдено.");
            return new ModelAndView("redirect:/admin/aircrafts");
        }
        ModelAndView mv = new ModelAndView("admin/aircraft-form");
        mv.addObject("title", "Редагування літака — Адмінпанель");
        mv.addObject("currentUser", principal.getUser());
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
                                       RedirectAttributes ra) {
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
    public ModelAndView deleteAircraft(@PathVariable Long id, RedirectAttributes ra) {
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

    @PostMapping("/{id}/maintenance")
    public ModelAndView sendToMaintenance(@PathVariable Long id, RedirectAttributes ra) {
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
    public ModelAndView activate(@PathVariable Long id, RedirectAttributes ra) {
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
    public ModelAndView retire(@PathVariable Long id, RedirectAttributes ra) {
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
    public ModelAndView listMaintenanceNeeded(@AuthenticationPrincipal SecurityUserDetails principal) {
        ModelAndView mv = new ModelAndView("admin/aircrafts");
        mv.addObject("title", "Літаки, що потребують ТО");
        mv.addObject("currentUser", principal.getUser());
        mv.addObject("aircrafts", aircraftService.getAircraftsNeedingMaintenance());
        mv.addObject("filterTitle", "Літаки, що потребують технічного обслуговування");
        return mv;
    }
}