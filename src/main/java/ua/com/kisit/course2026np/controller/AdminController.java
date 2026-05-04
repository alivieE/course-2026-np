package ua.com.kisit.course2026np.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ua.com.kisit.course2026np.security.SecurityUserDetails;
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

    @GetMapping
    public ModelAndView dashboard(@AuthenticationPrincipal SecurityUserDetails principal) {
        ModelAndView mv = new ModelAndView("admin/dashboard");
        mv.addObject("title", "Адмінпанель — Огляд");
        mv.addObject("currentUser", principal.getUser());
        mv.addObject("flightCount", flightService.getAllFlights().size());
        mv.addObject("aircraftCount", aircraftService.getAll().size());
        mv.addObject("maintenanceCount", aircraftService.getAircraftsNeedingMaintenance().size());
        mv.addObject("passengerCount", passengerService.getAll().size());
        mv.addObject("ticketCount", ticketService.getAll().size());
        return mv;
    }
}