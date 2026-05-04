package ua.com.kisit.course2026np.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.com.kisit.course2026np.entity.User;
import ua.com.kisit.course2026np.repository.UserRepository;

import java.util.List;

@Component
public class PasswordMigrationRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PasswordMigrationRunner.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordMigrationRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<User> all = userRepository.findAll();
        int migrated = 0;
        for (User user : all) {
            String password = user.getPassword();
            if (password == null) continue;
            if (!isBCryptHash(password)) {
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
                migrated++;
                log.info("Перехешовано пароль користувача: {}", user.getEmail());
            }
        }
        if (migrated > 0) {
            log.info("Міграція паролів завершена. Перехешовано записів: {}", migrated);
        } else {
            log.info("Усі паролі вже у форматі BCrypt — міграція не потрібна.");
        }
    }

    private boolean isBCryptHash(String value) {
        if (value == null || value.length() < 60) return false;
        return value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$");
    }
}