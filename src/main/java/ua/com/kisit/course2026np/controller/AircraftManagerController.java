package ua.com.kisit.course2026np.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(AircraftManagerController.class);
    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT");

    private final AircraftService aircraftService;

    public AircraftManagerController(AircraftService aircraftService) {
        this.aircraftService = aircraftService;
    }

    @GetMapping
    public ModelAndView listAircrafts(@AuthenticationPrincipal SecurityUserDetails principal) {
        log.debug("Перегляд списку літаків користувачем {}", principal.getUsername());
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
                                       @AuthenticationPrincipal SecurityUserDetails principal,
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
            Aircraft created = aircraftService.create(aircraft);
            auditLog.info("AIRCRAFT_CREATE actor={} aircraftId={} regNumber={} model={}",
                    principal.getUsername(), created.getId(), registrationNumber, model);
            ra.addFlashAttribute("toastType", "success");
            ra.addFlashAttribute("toastMessage", "Літак " + registrationNumber + " успішно створено.");
            return new ModelAndView("redirect:/admin/aircrafts");
        } catch (IllegalArgumentException ex) {
            log.warn("Не вдалось створити літак {}: {}", registrationNumber, ex.getMessage());
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
                                       @AuthenticationPrincipal SecurityUserDetails principal,
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
            auditLog.info("AIRCRAFT_UPDATE actor={} aircraftId={} regNumber={} status={}",
                    principal.getUsername(), id, registrationNumber, status);
            ra.addFlashAttribute("toastType", "success");
            ra.addFlashAttribute("toastMessage", "Літак " + registrationNumber + " оновлено.");
        } catch (RuntimeException ex) {
            log.warn("Помилка оновлення літака id={}: {}", id, ex.getMessage());
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Помилка оновлення: " + ex.getMessage());
        }
        return new ModelAndView("redirect:/admin/aircrafts");
    }

    @PostMapping("/{id}/delete")
    public ModelAndView deleteAircraft(@PathVariable Long id,
                                       @AuthenticationPrincipal SecurityUserDetails principal,
                                       RedirectAttributes ra) {
        try {
            aircraftService.delete(id);
            auditLog.info("AIRCRAFT_DELETE actor={} aircraftId={}", principal.getUsername(), id);
            ra.addFlashAttribute("toastType", "info");
            ra.addFlashAttribute("toastMessage", "Літак видалено.");
        } catch (RuntimeException ex) {
            log.warn("Помилка видалення літака id={}: {}", id, ex.getMessage());
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Помилка видалення: " + ex.getMessage());
        }
        return new ModelAndView("redirect:/admin/aircrafts");
    }


    @PostMapping("/{id}/maintenance")
    public ModelAndView sendToMaintenance(@PathVariable Long id,
                                          @AuthenticationPrincipal SecurityUserDetails principal,
                                          RedirectAttributes ra) {
        try {
            Aircraft aircraft = aircraftService.sendToMaintenance(id);
            auditLog.info("AIRCRAFT_MAINTENANCE actor={} aircraftId={} regNumber={}",
                    principal.getUsername(), id, aircraft.getRegistrationNumber());
            ra.addFlashAttribute("toastType", "info");
            ra.addFlashAttribute("toastMessage",
                    "Літак " + aircraft.getRegistrationNumber() + " відправлено на технічне обслуговування.");
        } catch (RuntimeException ex) {
            log.warn("Помилка відправлення літака id={} на ТО: {}", id, ex.getMessage());
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Помилка: " + ex.getMessage());
        }
        return new ModelAndView("redirect:/admin/aircrafts");
    }

    @PostMapping("/{id}/activate")
    public ModelAndView activate(@PathVariable Long id,
                                 @AuthenticationPrincipal SecurityUserDetails principal,
                                 RedirectAttributes ra) {
        try {
            Aircraft aircraft = aircraftService.activate(id);
            auditLog.info("AIRCRAFT_ACTIVATE actor={} aircraftId={} regNumber={}",
                    principal.getUsername(), id, aircraft.getRegistrationNumber());
            ra.addFlashAttribute("toastType", "success");
            ra.addFlashAttribute("toastMessage",
                    "Літак " + aircraft.getRegistrationNumber() + " повернуто в експлуатацію.");
        } catch (RuntimeException ex) {
            log.warn("Помилка активації літака id={}: {}", id, ex.getMessage());
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Помилка: " + ex.getMessage());
        }
        return new ModelAndView("redirect:/admin/aircrafts");
    }

    @PostMapping("/{id}/retire")
    public ModelAndView retire(@PathVariable Long id,
                               @AuthenticationPrincipal SecurityUserDetails principal,
                               RedirectAttributes ra) {
        try {
            Aircraft aircraft = aircraftService.retire(id);
            auditLog.info("AIRCRAFT_RETIRE actor={} aircraftId={} regNumber={}",
                    principal.getUsername(), id, aircraft.getRegistrationNumber());
            ra.addFlashAttribute("toastType", "info");
            ra.addFlashAttribute("toastMessage",
                    "Літак " + aircraft.getRegistrationNumber() + " списано з експлуатації.");
        } catch (RuntimeException ex) {
            log.warn("Помилка списання літака id={}: {}", id, ex.getMessage());
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Помилка: " + ex.getMessage());
        }
        return new ModelAndView("redirect:/admin/aircrafts");
    }

    @GetMapping("/maintenance-needed")
    public ModelAndView listMaintenanceNeeded(@AuthenticationPrincipal SecurityUserDetails principal) {
        log.info("Перегляд списку літаків що потребують ТО користувачем {}", principal.getUsername());
        ModelAndView mv = new ModelAndView("admin/aircrafts");
        mv.addObject("title", "Літаки, що потребують ТО");
        mv.addObject("currentUser", principal.getUser());
        mv.addObject("aircrafts", aircraftService.getAircraftsNeedingMaintenance());
        mv.addObject("filterTitle", "Літаки, що потребують технічного обслуговування");
        return mv;
    }
}
