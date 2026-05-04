package ua.com.kisit.course2026np.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.kisit.course2026np.entity.User;
import ua.com.kisit.course2026np.entity.UserRole;
import ua.com.kisit.course2026np.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

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
                .password(passwordEncoder.encode(password))
                .role(UserRole.DISPATCHER)
                .isActive(true)
                .build();
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}