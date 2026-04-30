package ua.com.kisit.course2026np.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.com.kisit.course2026np.entity.*;
import ua.com.kisit.course2026np.service.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final DateTimeFormatter INPUT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

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
                    .user(admin)
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

    @GetMapping("/aircrafts")
    public ModelAndView listAircrafts(HttpSession session, RedirectAttributes ra) {
        User admin = requireAdmin(session);
        if (admin == null) return new ModelAndView("redirect:/login");
        ModelAndView mv = new ModelAndView("admin/aircrafts");
        mv.addObject("title", "Адмінпанель — Літаки");
        mv.addObject("currentUser", admin);
        mv.addObject("aircrafts", aircraftService.getAll());
        return mv;
    }

    @GetMapping("/aircrafts/new")
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

    @PostMapping("/aircrafts")
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

    @GetMapping("/aircrafts/{id}/edit")
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

    @PostMapping("/aircrafts/{id}")
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

    @PostMapping("/aircrafts/{id}/delete")
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

    @GetMapping("/passengers")
    public ModelAndView listPassengers(HttpSession session) {
        User admin = requireAdmin(session);
        if (admin == null) return new ModelAndView("redirect:/login");
        ModelAndView mv = new ModelAndView("admin/passengers");
        mv.addObject("title", "Адмінпанель — Пасажири");
        mv.addObject("currentUser", admin);
        mv.addObject("passengers", passengerService.getAll());
        return mv;
    }

    @GetMapping("/passengers/new")
    public ModelAndView newPassengerForm(HttpSession session) {
        User admin = requireAdmin(session);
        if (admin == null) return new ModelAndView("redirect:/login");
        ModelAndView mv = new ModelAndView("admin/passenger-form");
        mv.addObject("title", "Створення пасажира — Адмінпанель");
        mv.addObject("currentUser", admin);
        mv.addObject("passenger", new Passenger());
        mv.addObject("isNew", true);
        mv.addObject("genders", Passenger.Gender.values());
        return mv;
    }

    @PostMapping("/passengers")
    public ModelAndView createPassenger(@RequestParam String firstName,
                                        @RequestParam String lastName,
                                        @RequestParam String passportNumber,
                                        @RequestParam String email,
                                        @RequestParam(required = false) String phone,
                                        @RequestParam(required = false) String dateOfBirth,
                                        @RequestParam(required = false) Passenger.Gender gender,
                                        HttpSession session,
                                        RedirectAttributes ra) {
        if (requireAdmin(session) == null) return new ModelAndView("redirect:/login");
        try {
            Passenger passenger = Passenger.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .passportNumber(passportNumber)
                    .email(email)
                    .phone(phone)
                    .dateOfBirth(dateOfBirth != null && !dateOfBirth.isBlank() ? LocalDate.parse(dateOfBirth) : null)
                    .gender(gender)
                    .build();
            passengerService.create(passenger);
            ra.addFlashAttribute("toastType", "success");
            ra.addFlashAttribute("toastMessage", "Пасажира " + lastName + " " + firstName + " створено.");
            return new ModelAndView("redirect:/admin/passengers");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", ex.getMessage());
            return new ModelAndView("redirect:/admin/passengers/new");
        }
    }

    @GetMapping("/passengers/{id}/edit")
    public ModelAndView editPassengerForm(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User admin = requireAdmin(session);
        if (admin == null) return new ModelAndView("redirect:/login");
        Optional<Passenger> pOpt = passengerService.getById(id);
        if (pOpt.isEmpty()) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Пасажира з ID " + id + " не знайдено.");
            return new ModelAndView("redirect:/admin/passengers");
        }
        ModelAndView mv = new ModelAndView("admin/passenger-form");
        mv.addObject("title", "Редагування пасажира — Адмінпанель");
        mv.addObject("currentUser", admin);
        mv.addObject("passenger", pOpt.get());
        mv.addObject("isNew", false);
        mv.addObject("genders", Passenger.Gender.values());
        return mv;
    }

    @PostMapping("/passengers/{id}")
    public ModelAndView updatePassenger(@PathVariable Long id,
                                        @RequestParam String firstName,
                                        @RequestParam String lastName,
                                        @RequestParam String passportNumber,
                                        @RequestParam String email,
                                        @RequestParam(required = false) String phone,
                                        @RequestParam(required = false) String dateOfBirth,
                                        @RequestParam(required = false) Passenger.Gender gender,
                                        HttpSession session,
                                        RedirectAttributes ra) {
        if (requireAdmin(session) == null) return new ModelAndView("redirect:/login");
        try {
            Passenger updated = Passenger.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .passportNumber(passportNumber)
                    .email(email)
                    .phone(phone)
                    .dateOfBirth(dateOfBirth != null && !dateOfBirth.isBlank() ? LocalDate.parse(dateOfBirth) : null)
                    .gender(gender)
                    .build();
            passengerService.update(id, updated);
            ra.addFlashAttribute("toastType", "success");
            ra.addFlashAttribute("toastMessage", "Пасажира оновлено.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Помилка оновлення: " + ex.getMessage());
        }
        return new ModelAndView("redirect:/admin/passengers");
    }

    @PostMapping("/passengers/{id}/delete")
    public ModelAndView deletePassenger(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (requireAdmin(session) == null) return new ModelAndView("redirect:/login");
        try {
            passengerService.delete(id);
            ra.addFlashAttribute("toastType", "info");
            ra.addFlashAttribute("toastMessage", "Пасажира видалено.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Помилка видалення: " + ex.getMessage());
        }
        return new ModelAndView("redirect:/admin/passengers");
    }

    @GetMapping("/tickets")
    public ModelAndView listTickets(HttpSession session) {
        User admin = requireAdmin(session);
        if (admin == null) return new ModelAndView("redirect:/login");
        ModelAndView mv = new ModelAndView("admin/tickets");
        mv.addObject("title", "Адмінпанель — Квитки");
        mv.addObject("currentUser", admin);
        mv.addObject("tickets", ticketService.getAll());
        return mv;
    }

    @GetMapping("/tickets/new")
    public ModelAndView newTicketForm(HttpSession session) {
        User admin = requireAdmin(session);
        if (admin == null) return new ModelAndView("redirect:/login");
        ModelAndView mv = new ModelAndView("admin/ticket-form");
        mv.addObject("title", "Створення квитка — Адмінпанель");
        mv.addObject("currentUser", admin);
        mv.addObject("ticket", new Ticket());
        mv.addObject("isNew", true);
        mv.addObject("statuses", Ticket.TicketStatus.values());
        mv.addObject("classes", Ticket.ServiceClass.values());
        mv.addObject("passengers", passengerService.getAll());
        mv.addObject("flights", flightService.getAllFlights());
        return mv;
    }

    @PostMapping("/tickets")
    public ModelAndView createTicket(@RequestParam String ticketNumber,
                                     @RequestParam String seatNumber,
                                     @RequestParam Ticket.ServiceClass serviceClass,
                                     @RequestParam BigDecimal price,
                                     @RequestParam Ticket.TicketStatus status,
                                     @RequestParam Long passengerId,
                                     @RequestParam Long flightId,
                                     HttpSession session,
                                     RedirectAttributes ra) {
        if (requireAdmin(session) == null) return new ModelAndView("redirect:/login");
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

    @GetMapping("/tickets/{id}/edit")
    public ModelAndView editTicketForm(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User admin = requireAdmin(session);
        if (admin == null) return new ModelAndView("redirect:/login");
        Optional<Ticket> tOpt = ticketService.getById(id);
        if (tOpt.isEmpty()) {
            ra.addFlashAttribute("toastType", "danger");
            ra.addFlashAttribute("toastMessage", "Квиток з ID " + id + " не знайдено.");
            return new ModelAndView("redirect:/admin/tickets");
        }
        ModelAndView mv = new ModelAndView("admin/ticket-form");
        mv.addObject("title", "Редагування квитка — Адмінпанель");
        mv.addObject("currentUser", admin);
        mv.addObject("ticket", tOpt.get());
        mv.addObject("isNew", false);
        mv.addObject("statuses", Ticket.TicketStatus.values());
        mv.addObject("classes", Ticket.ServiceClass.values());
        mv.addObject("passengers", passengerService.getAll());
        mv.addObject("flights", flightService.getAllFlights());
        return mv;
    }

    @PostMapping("/tickets/{id}")
    public ModelAndView updateTicket(@PathVariable Long id,
                                     @RequestParam String ticketNumber,
                                     @RequestParam String seatNumber,
                                     @RequestParam Ticket.ServiceClass serviceClass,
                                     @RequestParam BigDecimal price,
                                     @RequestParam Ticket.TicketStatus status,
                                     @RequestParam Long passengerId,
                                     @RequestParam Long flightId,
                                     HttpSession session,
                                     RedirectAttributes ra) {
        if (requireAdmin(session) == null) return new ModelAndView("redirect:/login");
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

    @PostMapping("/tickets/{id}/delete")
    public ModelAndView deleteTicket(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (requireAdmin(session) == null) return new ModelAndView("redirect:/login");
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
}