package ua.com.kisit.course2026np.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.kisit.course2026np.entity.Crew;

import java.util.Optional;

@Repository
public interface CrewRepository extends JpaRepository<Crew, Long> {
    Optional<Crew> findByFlightId(Long flightId);
    boolean existsByFlightId(Long flightId);
    void deleteByFlightId(Long flightId);
}