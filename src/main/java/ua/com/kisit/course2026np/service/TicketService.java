package ua.com.kisit.course2026np.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.kisit.course2026np.entity.Ticket;
import ua.com.kisit.course2026np.repository.TicketRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<Ticket> getAll() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> getById(Long id) {
        return ticketRepository.findById(id);
    }

    public List<Ticket> getByPassenger(Long passengerId) {
        return ticketRepository.findByPassengerId(passengerId);
    }

    public List<Ticket> getByFlight(Long flightId) {
        return ticketRepository.findByFlightId(flightId);
    }

    @Transactional
    public Ticket create(Ticket ticket) {
        if (ticketRepository.existsByTicketNumber(ticket.getTicketNumber())) {
            throw new IllegalArgumentException(
                    "Квиток з номером " + ticket.getTicketNumber() + " вже існує");
        }
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket update(Long id, Ticket updated) {
        return ticketRepository.findById(id).map(t -> {
            t.setTicketNumber(updated.getTicketNumber());
            t.setSeatNumber(updated.getSeatNumber());
            t.setServiceClass(updated.getServiceClass());
            t.setPrice(updated.getPrice());
            t.setStatus(updated.getStatus());
            t.setPassenger(updated.getPassenger());
            t.setFlight(updated.getFlight());
            return ticketRepository.save(t);
        }).orElseThrow(() ->
                new RuntimeException("Квиток не знайдено: " + id));
    }

    @Transactional
    public void delete(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new RuntimeException("Квиток не знайдено: " + id);
        }
        ticketRepository.deleteById(id);
    }

    @Transactional
    public Ticket confirmTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Квиток не знайдено: " + ticketId));
        if (ticket.getStatus() != Ticket.TicketStatus.RESERVED) {
            throw new IllegalStateException(
                    "Підтвердити можна лише заброньований квиток. Поточний статус: " + ticket.getStatus());
        }
        ticket.setStatus(Ticket.TicketStatus.CONFIRMED);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket cancelTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Квиток не знайдено: " + ticketId));
        ticket.cancel();
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket markAsUsed(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Квиток не знайдено: " + ticketId));
        if (ticket.getStatus() != Ticket.TicketStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Позначити як використаний можна лише підтверджений квиток. Поточний статус: " + ticket.getStatus());
        }
        ticket.setStatus(Ticket.TicketStatus.USED);
        return ticketRepository.save(ticket);
    }

    public BigDecimal calculateRevenueByFlight(Long flightId) {
        return ticketRepository.calculateRevenueByFlight(flightId);
    }

    public long countSoldTicketsByFlight(Long flightId) {
        return ticketRepository.countByFlightIdAndStatus(flightId, Ticket.TicketStatus.CONFIRMED);
    }
}