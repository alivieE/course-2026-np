package ua.com.kisit.course2026np.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.kisit.course2026np.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Пошук за email (для автентифікації)
    Optional<User> findByEmail(String email);

    // Всі активні користувачі
    List<User> findByIsActiveTrue();

    // Користувачі за роллю (ADMIN / DISPATCHER)
    List<User> findByRole(String role);

    // Перевірка унікальності email
    boolean existsByEmail(String email);
}