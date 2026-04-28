package ua.com.kisit.course2026np.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.com.kisit.course2026np.entity.Aircraft;

import java.util.List;
import java.util.Optional;

@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, Long> {
    Optional<Aircraft> findByRegistrationNumber(String registrationNumber);
    List<Aircraft> findByModelContainingIgnoreCase(String model);
    List<Aircraft> findByStatus(Aircraft.AircraftStatus status);
    List<Aircraft> findByManufacturer(String manufacturer);
    boolean existsByRegistrationNumber(String registrationNumber);
    long countByStatus(Aircraft.AircraftStatus status);
    @Query("SELECT a FROM Aircraft a WHERE a.totalSeats > :minSeats ORDER BY a.totalSeats DESC")
    List<Aircraft> findLargeAircrafts(@Param("minSeats") Integer minSeats);
    @Query("SELECT a FROM Aircraft a WHERE a.yearOfManufacture <= :year")
    List<Aircraft> findOlderThan(@Param("year") Integer year);
}