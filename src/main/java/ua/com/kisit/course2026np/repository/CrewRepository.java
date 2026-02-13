package ua.com.kisit.course2026np.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.kisit.course2026np.entity.Crew;

import java.util.Optional;

@Repository
public interface CrewRepository extends JpaRepository<Crew, Long> {

    // Бригада для конкретного рейсу (flight_id_UNIQUE)
    Optional<Crew> findByFlightId(Long flightId);

    // Чи існує вже бригада для цього рейсу
    boolean existsByFlightId(Long flightId);

    // Видалити бригаду разом з усіма членами (orphanRemoval = true)
    void deleteByFlightId(Long flightId);
}