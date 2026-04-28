package ua.com.kisit.course2026np.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.kisit.course2026np.entity.Ticket;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTicketNumber(String ticketNumber);
    List<Ticket> findByPassengerId(Long passengerId);
    List<Ticket> findByFlightId(Long flightId);
    List<Ticket> findByStatus(Ticket.TicketStatus status);
    boolean existsByTicketNumber(String ticketNumber);
    long countByFlightId(Long flightId);
}