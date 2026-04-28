package ua.com.kisit.course2026np.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.kisit.course2026np.entity.Passenger;

import java.util.List;
import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    Optional<Passenger> findByPassportNumber(String passportNumber);
    Optional<Passenger> findByEmail(String email);
    List<Passenger> findByLastNameContainingIgnoreCase(String lastName);
    boolean existsByPassportNumber(String passportNumber);
    boolean existsByEmail(String email);
}