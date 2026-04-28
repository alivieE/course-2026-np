package ua.com.kisit.course2026np.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.com.kisit.course2026np.entity.Passenger;
import ua.com.kisit.course2026np.entity.Ticket;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    Optional<Passenger> findByPassportNumber(String passportNumber);
    Optional<Passenger> findByEmail(String email);
    List<Passenger> findByLastNameContainingIgnoreCase(String lastName);
    boolean existsByPassportNumber(String passportNumber);
    boolean existsByEmail(String email);
    @Query("SELECT DISTINCT p FROM Passenger p JOIN p.tickets t WHERE t.status = :status")
    List<Passenger> findPassengersWithStatus(@Param("status") Ticket.TicketStatus status);
    default List<Passenger> findPassengersWithConfirmedTickets() {
        return findPassengersWithStatus(Ticket.TicketStatus.CONFIRMED);
    }
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.passenger.id = :passengerId")
    long countTicketsByPassenger(@Param("passengerId") Long passengerId);
}