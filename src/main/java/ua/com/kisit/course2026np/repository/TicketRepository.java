package ua.com.kisit.course2026np.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.com.kisit.course2026np.entity.Ticket;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTicketNumber(String ticketNumber);
    List<Ticket> findByPassengerId(Long passengerId);
    List<Ticket> findByFlightId(Long flightId);
    List<Ticket> findByStatus(Ticket.TicketStatus status);
    List<Ticket> findByServiceClass(Ticket.ServiceClass serviceClass);
    boolean existsByTicketNumber(String ticketNumber);
    long countByFlightId(Long flightId);
    long countByFlightIdAndStatus(Long flightId, Ticket.TicketStatus status);
    @Query("SELECT COALESCE(SUM(t.price), 0) FROM Ticket t " +
            "WHERE t.flight.id = :flightId AND t.status = :status")
    BigDecimal calculateRevenueByFlightAndStatus(@Param("flightId") Long flightId,
                                                 @Param("status") Ticket.TicketStatus status);
    default BigDecimal calculateRevenueByFlight(Long flightId) {
        return calculateRevenueByFlightAndStatus(flightId, Ticket.TicketStatus.CONFIRMED);
    }
    @Query("SELECT t FROM Ticket t WHERE t.price BETWEEN :minPrice AND :maxPrice " +
            "ORDER BY t.price ASC")
    List<Ticket> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                  @Param("maxPrice") BigDecimal maxPrice);
}