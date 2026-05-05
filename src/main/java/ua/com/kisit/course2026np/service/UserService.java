package ua.com.kisit.course2026np.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.kisit.course2026np.entity.User;
import ua.com.kisit.course2026np.entity.UserRole;
import ua.com.kisit.course2026np.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(String firstName, String lastName, String email, String password) {
        log.debug("Реєстрація нового користувача: email={}, role={}", email, UserRole.DISPATCHER);
        if (userRepository.existsByEmail(email)) {
            log.warn("Спроба реєстрації з існуючим email: {}", email);
            throw new IllegalArgumentException(
                    "Користувач з email " + email + " вже зареєстрований");
        }
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(UserRole.DISPATCHER)
                .isActive(true)
                .build();
        User saved = userRepository.save(user);
        log.info("Зареєстровано нового користувача: id={}, email={}", saved.getId(), email);
        return saved;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
