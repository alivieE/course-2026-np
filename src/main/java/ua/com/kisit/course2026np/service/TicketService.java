package ua.com.kisit.course2026np.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.kisit.course2026np.entity.Ticket;
import ua.com.kisit.course2026np.repository.TicketRepository;

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
}
