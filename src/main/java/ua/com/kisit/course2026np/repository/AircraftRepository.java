package ua.com.kisit.course2026np.repository;

import org.springframework.data.jpa.repository.JpaRepository;
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
}