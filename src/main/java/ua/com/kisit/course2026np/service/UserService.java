package ua.com.kisit.course2026np.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.kisit.course2026np.entity.User;
import ua.com.kisit.course2026np.entity.UserRole;
import ua.com.kisit.course2026np.repository.UserRepository;

import java.util.Optional;

/**
 * Сервіс для роботи з користувачами:
 * автентифікація (login), реєстрація (register), пошук за email.
 * Використовується контролером AuthController, що зберігає
 * користувача в HttpSession після успішного входу.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Перевірка існування користувача за парою email + password.
     * Повертає Optional<User>: empty — якщо не знайдено (невірні дані),
     * або сам об'єкт User — якщо автентифікацію пройдено.
     */
    public Optional<User> login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return Optional.empty();
        }
        return userRepository.findByEmailAndPassword(email.trim(), password);
    }

    /**
     * Реєстрація нового користувача.
     * Якщо email вже існує — кидає IllegalArgumentException.
     * Новим користувачам за замовчуванням призначається роль DISPATCHER.
     */
    @Transactional
    public User register(String firstName, String lastName, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Користувач з email " + email + " вже зареєстрований");
        }
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(password)
                .role(UserRole.DISPATCHER)
                .isActive(true)
                .build();
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}