package ua.com.kisit.course2026np.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.kisit.course2026np.entity.Passenger;
import ua.com.kisit.course2026np.entity.User;
import ua.com.kisit.course2026np.repository.PassengerRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PassengerService {

    private final PassengerRepository passengerRepository;

    public PassengerService(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    public List<Passenger> getAll() {
        return passengerRepository.findAll();
    }

    public Optional<Passenger> getById(Long id) {
        return passengerRepository.findById(id);
    }

    @Transactional
    public Passenger create(Passenger passenger) {
        validateUniqueness(passenger, null);
        return passengerRepository.save(passenger);
    }

    @Transactional
    public Passenger update(Long id, Passenger updated) {
        return passengerRepository.findById(id).map(p -> {
            // Якщо паспорт або email змінилися — перевіряємо унікальність
            if (!p.getPassportNumber().equals(updated.getPassportNumber()) ||
                    !p.getEmail().equals(updated.getEmail())) {
                validateUniqueness(updated, id);
            }
            p.setFirstName(updated.getFirstName());
            p.setLastName(updated.getLastName());
            p.setPassportNumber(updated.getPassportNumber());
            p.setEmail(updated.getEmail());
            p.setPhone(updated.getPhone());
            p.setDateOfBirth(updated.getDateOfBirth());
            p.setGender(updated.getGender());
            return passengerRepository.save(p);
        }).orElseThrow(() ->
                new RuntimeException("Пасажира не знайдено: " + id));
    }

    @Transactional
    public void delete(Long id) {
        if (!passengerRepository.existsById(id)) {
            throw new RuntimeException("Пасажира не знайдено: " + id);
        }
        passengerRepository.deleteById(id);
    }

    public List<Passenger> searchByLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            return getAll();
        }
        return passengerRepository.findByLastNameContainingIgnoreCase(lastName.trim());
    }

    public Optional<Passenger> findByPassport(String passportNumber) {
        return passengerRepository.findByPassportNumber(passportNumber);
    }

    public Optional<Passenger> findByEmail(String email) {
        return passengerRepository.findByEmail(email);
    }

    @Transactional
    public Passenger getOrCreateForUser(User user) {
        return passengerRepository.findByEmail(user.getEmail())
                .orElseGet(() -> create(Passenger.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .passportNumber("USR" + String.format("%05d", user.getId()))
                        .build()));
    }

    private void validateUniqueness(Passenger passenger, Long currentId) {
        Optional<Passenger> byPassport = passengerRepository.findByPassportNumber(
                passenger.getPassportNumber());
        if (byPassport.isPresent() && !byPassport.get().getId().equals(currentId)) {
            throw new IllegalArgumentException(
                    "Пасажир з паспортом " + passenger.getPassportNumber() + " вже існує");
        }
        Optional<Passenger> byEmail = passengerRepository.findByEmail(passenger.getEmail());
        if (byEmail.isPresent() && !byEmail.get().getId().equals(currentId)) {
            throw new IllegalArgumentException(
                    "Пасажир з email " + passenger.getEmail() + " вже існує");
        }
    }
}