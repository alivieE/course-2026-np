package ua.com.kisit.course2026np.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.kisit.course2026np.entity.Passenger;
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
        if (passengerRepository.existsByPassportNumber(passenger.getPassportNumber())) {
            throw new IllegalArgumentException(
                    "Пасажир з паспортом " + passenger.getPassportNumber() + " вже існує");
        }
        if (passengerRepository.existsByEmail(passenger.getEmail())) {
            throw new IllegalArgumentException(
                    "Пасажир з email " + passenger.getEmail() + " вже існує");
        }
        return passengerRepository.save(passenger);
    }

    @Transactional
    public Passenger update(Long id, Passenger updated) {
        return passengerRepository.findById(id).map(p -> {
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
}
