package ua.com.kisit.course2026np.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ua.com.kisit.course2026np.entity.User;
import ua.com.kisit.course2026np.entity.UserRole;
import ua.com.kisit.course2026np.service.AircraftService;
import ua.com.kisit.course2026np.service.FlightService;
import ua.com.kisit.course2026np.service.PassengerService;
import ua.com.kisit.course2026np.service.TicketService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final FlightService flightService;
    private final AircraftService aircraftService;
    private final PassengerService passengerService;
    private final TicketService ticketService;

    public AdminController(FlightService flightService,
                           AircraftService aircraftService,
                           PassengerService passengerService,
                           TicketService ticketService) {
        this.flightService = flightService;
        this.aircraftService = aircraftService;
        this.passengerService = passengerService;
        this.ticketService = ticketService;
    }

    private User requireAdmin(HttpSession session) {
        Object attr = session.getAttribute(AuthController.SESSION_USER_ATTR);
        if (!(attr instanceof User)) return null;
        User user = (User) attr;
        return user.getRole() == UserRole.ADMIN ? user : null;
    }

    @GetMapping
    public ModelAndView dashboard(HttpSession session) {
        User admin = requireAdmin(session);
        if (admin == null) return new ModelAndView("redirect:/login");

        ModelAndView mv = new ModelAndView("admin/dashboard");
        mv.addObject("title", "Адмінпанель — Огляд");
        mv.addObject("currentUser", admin);
        mv.addObject("flightCount", flightService.getAllFlights().size());
        mv.addObject("aircraftCount", aircraftService.getAll().size());
        mv.addObject("maintenanceCount", aircraftService.getAircraftsNeedingMaintenance().size());
        mv.addObject("passengerCount", passengerService.getAll().size());
        mv.addObject("ticketCount", ticketService.getAll().size());
        return mv;
    }
}
