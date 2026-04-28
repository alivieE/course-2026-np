package ua.com.kisit.course2026np.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.com.kisit.course2026np.entity.Flight;
import ua.com.kisit.course2026np.entity.FlightStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    Optional<Flight> findByFlightNumber(String flightNumber);
    boolean existsByFlightNumber(String flightNumber);
    List<Flight> findByStatus(String status);
    @Query("SELECT f FROM Flight f WHERE f.status = :status ORDER BY f.departureTime ASC")
    List<Flight> findByStatusOrderByDeparture(@Param("status") String status);
    List<Flight> findByDepartureCityAndArrivalCity(String from, String to);
    List<Flight> findByDepartureTimeBetween(LocalDateTime from, LocalDateTime to);
    List<Flight> findByUserId(Long userId);
    long countByStatus(FlightStatus status);
    @Query("SELECT f FROM Flight f WHERE f.status = ua.com.kisit.course2026np.entity.FlightStatus.PLANNED " +
            "AND f.departureTime > :now ORDER BY f.departureTime ASC")
    List<Flight> findUpcomingFlights(@Param("now") LocalDateTime now);
    @Query("SELECT f FROM Flight f WHERE LOWER(f.flightNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(f.departureCity) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(f.arrivalCity) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Flight> searchFlights(@Param("search") String search);
}