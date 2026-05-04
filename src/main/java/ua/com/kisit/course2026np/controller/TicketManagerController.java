package ua.com.kisit.course2026np.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.com.kisit.course2026np.entity.Flight;
import ua.com.kisit.course2026np.entity.Passenger;
import ua.com.kisit.course2026np.entity.Ticket;
import ua.com.kisit.course2026np.security.SecurityUserDetails;
import ua.com.kisit.course2026np.service.FlightService;
import ua.com.kisit.course2026np.service.PassengerService;
import ua.com.kisit.course2026np.service.TicketService;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/admin/tickets")
public class TicketManagerController {

    private final TicketService ticketService;
    private final PassengerService passengerService;
    private final FlightService flightService;

    public TicketManagerController(TicketService ticketService,
                                   PassengerService passengerService,
                                   FlightService flightService) {
        this.ticketService = ticketService;
        this.passengerService = passengerService;
        this.flightService = flightService;
    }

    @GetMapping
    public ModelAndView listTickets(@AuthenticationPrincipal SecurityUserDetails principal) {
        ModelAndView mv = new ModelAndView("admin/tickets");
        mv.addObject("title", "Адмінпанель — Квитки");
        mv.addObject("currentUser", principal.getUser());
        mv.addObject("tickets", ticketService.getAll());
        return mv;
    }

    @GetMapping("/new")
    public ModelAndView newTicketForm(@AuthenticationPrincipal SecurityUserDetails principal) {
        ModelAndView mv = new ModelAndView("admin/ticket-form");
        mv.addObject("title", "Створення квитка — Адмінпанель");
        mv.addObject("currentUser", principal.getUser());
        mv.addObject("ticket", new Ticket());
        mv.addObject("isNew", true);
        mv.addObject("statuses", Ticket.TicketStatus.values());
        mv.addObject("classes", Ticket.ServiceClass.values());
        mv.addObject("passengers", passengerService.getAll());
        mv.addObject("flights", flightService.getAllFlights());
        return mv;
    }

    @PostMapping
    public ModelAndView createTicket(@RequestParam String ticketNumber,
                                     @RequestParam String seatNumber,
                                     @RequestParam Ticket.ServiceClass serviceClass,
                                     @RequestParam BigDecimal price,
                                     @RequestParam Ticket.TicketStatus status,
                                     @RequestParam Long passengerId,
                                     @RequestParam Long flightId,
                                     RedirectAttributes ra) {
        try {
            Passenger passenger = passengerService.getById(passengerId)
                    .orElseThrow(() -> new IllegalArgumentException("Пасажира не знайдено"));
            Flight flight = flightService.getById(flightId)
                    .orElseThrow(() -> new IllegalArgumentException("Рейс не знайдено"));

            Ticket ticket = Ticket.builder()
                    .ticketNumber(ticketNumber)
                    .seatNumber(seatNumber)
                    .serviceClass(serviceClass)
                    .price(price)
                    .status(status)
                    .passenger(passenger)
                    .flight(flight)
                    .build();
            ticketService.create(ticket);
            ra.addFlashAttribute("toastType", "success");
            ra.addFlashAttribute("toastMessage", "Квиток " + ticketNumber + " успішно створено.");
            return new ModelAndView("redirect:/admin/tickets");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", ex.getMessage());
            return new ModelAndView("redirect:/admin/tickets/new");
        }
    }

    @GetMapping("/{id}/edit")
    public ModelAndView editTicketForm(@PathVariable Long id,
                                       @AuthenticationPrincipal SecurityUserDetails principal,
                                       RedirectAttributes ra) {
        Optional<Ticket> tOpt = ticketService.getById(id);
        if (tOpt.isEmpty()) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Квиток з ID " + id + " не знайдено.");
            return new ModelAndView("redirect:/admin/tickets");
        }
        ModelAndView mv = new ModelAndView("admin/ticket-form");
        mv.addObject("title", "Редагування квитка — Адмінпанель");
        mv.addObject("currentUser", principal.getUser());
        mv.addObject("ticket", tOpt.get());
        mv.addObject("isNew", false);
        mv.addObject("statuses", Ticket.TicketStatus.values());
        mv.addObject("classes", Ticket.ServiceClass.values());
        mv.addObject("passengers", passengerService.getAll());
        mv.addObject("flights", flightService.getAllFlights());
        return mv;
    }

    @PostMapping("/{id}")
    public ModelAndView updateTicket(@PathVariable Long id,
                                     @RequestParam String ticketNumber,
                                     @RequestParam String seatNumber,
                                     @RequestParam Ticket.ServiceClass serviceClass,
                                     @RequestParam BigDecimal price,
                                     @RequestParam Ticket.TicketStatus status,
                                     @RequestParam Long passengerId,
                                     @RequestParam Long flightId,
                                     RedirectAttributes ra) {
        try {
            Passenger passenger = passengerService.getById(passengerId)
                    .orElseThrow(() -> new IllegalArgumentException("Пасажира не знайдено"));
            Flight flight = flightService.getById(flightId)
                    .orElseThrow(() -> new IllegalArgumentException("Рейс не знайдено"));

            Ticket updated = Ticket.builder()
                    .ticketNumber(ticketNumber)
                    .seatNumber(seatNumber)
                    .serviceClass(serviceClass)
                    .price(price)
                    .status(status)
                    .passenger(passenger)
                    .flight(flight)
                    .build();
            ticketService.update(id, updated);
            ra.addFlashAttribute("toastType", "success");
            ra.addFlashAttribute("toastMessage", "Квиток оновлено.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Помилка оновлення: " + ex.getMessage());
        }
        return new ModelAndView("redirect:/admin/tickets");
    }

    @PostMapping("/{id}/delete")
    public ModelAndView deleteTicket(@PathVariable Long id, RedirectAttributes ra) {
        try {
            ticketService.delete(id);
            ra.addFlashAttribute("toastType", "info");
            ra.addFlashAttribute("toastMessage", "Квиток видалено.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Помилка видалення: " + ex.getMessage());
        }
        return new ModelAndView("redirect:/admin/tickets");
    }

    @PostMapping("/{id}/confirm")
    public ModelAndView confirmTicket(@PathVariable Long id, RedirectAttributes ra) {
        try {
            Ticket ticket = ticketService.confirmTicket(id);
            ra.addFlashAttribute("toastType", "success");
            ra.addFlashAttribute("toastMessage",
                    "Квиток " + ticket.getTicketNumber() + " підтверджено.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Помилка підтвердження: " + ex.getMessage());
        }
        return new ModelAndView("redirect:/admin/tickets");
    }

    @PostMapping("/{id}/cancel")
    public ModelAndView cancelTicket(@PathVariable Long id, RedirectAttributes ra) {
        try {
            Ticket ticket = ticketService.cancelTicket(id);
            ra.addFlashAttribute("toastType", "info");
            ra.addFlashAttribute("toastMessage",
                    "Квиток " + ticket.getTicketNumber() + " скасовано.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Помилка скасування: " + ex.getMessage());
        }
        return new ModelAndView("redirect:/admin/tickets");
    }

    @PostMapping("/{id}/use")
    public ModelAndView markAsUsed(@PathVariable Long id, RedirectAttributes ra) {
        try {
            Ticket ticket = ticketService.markAsUsed(id);
            ra.addFlashAttribute("toastType", "info");
            ra.addFlashAttribute("toastMessage",
                    "Квиток " + ticket.getTicketNumber() + " позначено як використаний.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Помилка: " + ex.getMessage());
        }
        return new ModelAndView("redirect:/admin/tickets");
    }

    @GetMapping("/revenue/{flightId}")
    public ModelAndView showFlightRevenue(@PathVariable Long flightId, RedirectAttributes ra) {
        Optional<Flight> flightOpt = flightService.getById(flightId);
        if (flightOpt.isEmpty()) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Рейс не знайдено.");
            return new ModelAndView("redirect:/admin/tickets");
        }

        BigDecimal revenue = ticketService.calculateRevenueByFlight(flightId);
        long sold = ticketService.countSoldTicketsByFlight(flightId);

        ra.addFlashAttribute("toastType", "info");
        ra.addFlashAttribute("toastMessage",
                "Рейс " + flightOpt.get().getFlightNumber() + ": продано " + sold +
                        " квитків, дохід — " + (revenue != null ? revenue : BigDecimal.ZERO) + " ₴");
        return new ModelAndView("redirect:/admin/tickets");
    }
}