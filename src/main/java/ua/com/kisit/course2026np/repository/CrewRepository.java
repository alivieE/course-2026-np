package ua.com.kisit.course2026np.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.com.kisit.course2026np.entity.Crew;

import java.util.Optional;

@Repository
public interface CrewRepository extends JpaRepository<Crew, Long> {

    @Query("SELECT c FROM Crew c WHERE c.flight.id = :flightId")
    Optional<Crew> findByFlightId(@Param("flightId") Long flightId);

    @Query("SELECT COUNT(c) > 0 FROM Crew c WHERE c.flight.id = :flightId")
    boolean existsByFlightId(@Param("flightId") Long flightId);

    @Modifying
    @Query("DELETE FROM Crew c WHERE c.flight.id = :flightId")
    void deleteByFlightId(@Param("flightId") Long flightId);
}
