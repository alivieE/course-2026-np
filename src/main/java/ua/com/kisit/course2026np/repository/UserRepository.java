package ua.com.kisit.course2026np.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.com.kisit.course2026np.entity.User;
import ua.com.kisit.course2026np.entity.UserRole;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndPassword(String email, String password);
    List<User> findByIsActiveTrue();
    List<User> findByRole(UserRole role);
    boolean existsByEmail(String email);
    long countByIsActiveTrue();
    List<User> findByLastNameContainingIgnoreCase(String lastNamePart);
    @Query("SELECT DISTINCT u FROM User u WHERE u.flights IS NOT EMPTY")
    List<User> findUsersWithFlights();
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.isActive = true")
    long countActiveUsersByRole(@Param("role") UserRole role);
}