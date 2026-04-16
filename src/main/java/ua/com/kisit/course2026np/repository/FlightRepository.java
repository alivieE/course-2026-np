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
public interface FlightRepository extends JpaRepository<Flight, Integer> {

    // Пошук рейсу за унікальним номером
    Optional<Flight> findByFlightNumber(String flightNumber);

    // Фільтр за статусом (індекс idx_status)
    List<Flight> findByStatus(String status);

    // Рейси за адміністратором (індекс idx_user_id)
    List<Flight> findByUserId(Integer userId);

    // Рейси за маршрутом
    List<Flight> findByDepartureCityAndArrivalCity(
            String departureCity, String arrivalCity);

    // JPQL: заплановані рейси, відсортовані за часом відправлення
    @Query("SELECT f FROM Flight f WHERE f.status = :status ORDER BY f.departureTime ASC")
    List<Flight> findByStatusOrderByDeparture(@Param("status") String status);

    // Рейси у діапазоні дат (індекс idx_departure_time)
    List<Flight> findByDepartureTimeBetween(LocalDateTime from, LocalDateTime to);

    // Перевірка унікальності номера рейсу
    boolean existsByFlightNumber(String flightNumber);
}