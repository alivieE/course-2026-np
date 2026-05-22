package ua.com.kisit.course2026np.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ua.com.kisit.course2026np.entity.Passenger;
import ua.com.kisit.course2026np.entity.Ticket;
import ua.com.kisit.course2026np.entity.User;
import ua.com.kisit.course2026np.security.SecurityUserDetails;
import ua.com.kisit.course2026np.service.FlightService;
import ua.com.kisit.course2026np.service.PassengerService;
import ua.com.kisit.course2026np.service.TicketService;

import java.util.List;
import java.util.Objects;

@Controller
public class MyTicketsController {

    private final TicketService ticketService;
    private final PassengerService passengerService;
    private final FlightService flightService;

    public MyTicketsController(TicketService ticketService,
                               PassengerService passengerService,
                               FlightService flightService) {
        this.ticketService = ticketService;
        this.passengerService = passengerService;
        this.flightService = flightService;
    }

    @GetMapping("/my-tickets")
    public ModelAndView myTickets(@AuthenticationPrincipal SecurityUserDetails principal,
                                  @RequestParam(required = false) Long flightId) {
        User user = principal.getUser();
        Passenger passenger = passengerService.getOrCreateForUser(user);
        List<Ticket> tickets = ticketService.getByPassenger(passenger.getId());

        if (flightId != null) {
            tickets = tickets.stream()
                    .filter(t -> t.getFlight() != null && Objects.equals(t.getFlight().getId(), flightId))
                    .toList();
        }

        ModelAndView mv = new ModelAndView("my-tickets");
        mv.addObject("title", "Мої квитки - SkyAirlines");
        mv.addObject("currentUser", user);
        mv.addObject("passenger", passenger);
        mv.addObject("tickets", tickets);
        mv.addObject("flights", flightService.getAllFlights());
        mv.addObject("filterFlightId", flightId);
        return mv;
    }
}
