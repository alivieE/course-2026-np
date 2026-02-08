package ua.com.kisit.course2026np.repository;

import ua.com.kisit.course2026np.entity.Flight;
import ua.com.kisit.course2026np.entity.FlightStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    List<Flight> findByStatus(FlightStatus status);
    List<Flight> findByDepartureCity(String departureCity);
    List<Flight> findByArrivalCity(String arrivalCity);
    List<Flight> findByDepartureTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Flight> findByFlightNumberContaining(String flightNumber);
}