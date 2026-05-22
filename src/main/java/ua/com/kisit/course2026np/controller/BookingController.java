package ua.com.kisit.course2026np.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.com.kisit.course2026np.entity.Flight;
import ua.com.kisit.course2026np.entity.Passenger;
import ua.com.kisit.course2026np.entity.Ticket;
import ua.com.kisit.course2026np.entity.User;
import ua.com.kisit.course2026np.security.SecurityUserDetails;
import ua.com.kisit.course2026np.service.FlightService;
import ua.com.kisit.course2026np.service.PassengerService;
import ua.com.kisit.course2026np.service.TicketService;

import java.math.BigDecimal;

@Controller
@RequestMapping("/booking")
public class BookingController {

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    private final TicketService ticketService;
    private final PassengerService passengerService;
    private final FlightService flightService;

    public BookingController(TicketService ticketService,
                             PassengerService passengerService,
                             FlightService flightService) {
        this.ticketService = ticketService;
        this.passengerService = passengerService;
        this.flightService = flightService;
    }

    @GetMapping
    public ModelAndView bookingPage(@AuthenticationPrincipal SecurityUserDetails principal,
                                    @RequestParam(required = false) Long flightId) {
        User user = principal.getUser();
        Passenger passenger = passengerService.getOrCreateForUser(user);

        ModelAndView mv = new ModelAndView("booking");
        mv.addObject("title", "Оформлення квитка - SkyAirlines");
        mv.addObject("currentUser", user);
        mv.addObject("passenger", passenger);
        mv.addObject("flights", flightService.getAllFlights());
        mv.addObject("classes", Ticket.ServiceClass.values());
        mv.addObject("selectedFlightId", flightId);
        return mv;
    }

    @PostMapping
    public ModelAndView createBooking(@RequestParam String ticketNumber,
                                      @RequestParam String seatNumber,
                                      @RequestParam Ticket.ServiceClass serviceClass,
                                      @RequestParam BigDecimal price,
                                      @RequestParam Long flightId,
                                      @AuthenticationPrincipal SecurityUserDetails principal,
                                      RedirectAttributes ra) {
        User user = principal.getUser();
        try {
            Passenger passenger = passengerService.getOrCreateForUser(user);
            Flight flight = flightService.getById(flightId)
                    .orElseThrow(() -> new IllegalArgumentException("Рейс не знайдено"));

            Ticket ticket = Ticket.builder()
                    .ticketNumber(ticketNumber)
                    .seatNumber(seatNumber)
                    .serviceClass(serviceClass)
                    .price(price)
                    .status(Ticket.TicketStatus.RESERVED)
                    .passenger(passenger)
                    .flight(flight)
                    .build();

            Ticket created = ticketService.create(ticket);
            log.info("Користувач {} оформив квиток {} на рейс {}",
                    user.getEmail(), created.getTicketNumber(), flight.getFlightNumber());

            ra.addFlashAttribute("toastType", "success");
            ra.addFlashAttribute("toastMessage",
                    "Квиток " + ticketNumber + " успішно заброньовано на рейс " + flight.getFlightNumber() + ".");
            return new ModelAndView("redirect:/my-tickets?flightId=" + flightId);
        } catch (IllegalArgumentException ex) {
            log.warn("Не вдалось оформити квиток для {}: {}", user.getEmail(), ex.getMessage());
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", ex.getMessage());
            return new ModelAndView("redirect:/booking" + (flightId != null ? "?flightId=" + flightId : ""));
        }
    }
}
