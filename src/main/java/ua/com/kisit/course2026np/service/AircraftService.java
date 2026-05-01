package ua.com.kisit.course2026np.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.kisit.course2026np.entity.Aircraft;
import ua.com.kisit.course2026np.repository.AircraftRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AircraftService {

    private final AircraftRepository aircraftRepository;

    public AircraftService(AircraftRepository aircraftRepository) {
        this.aircraftRepository = aircraftRepository;
    }

    public List<Aircraft> getAll() {
        return aircraftRepository.findAll();
    }

    public Optional<Aircraft> getById(Long id) {
        return aircraftRepository.findById(id);
    }

    public List<Aircraft> getActiveAircrafts() {
        return aircraftRepository.findByStatus(Aircraft.AircraftStatus.ACTIVE);
    }

    @Transactional
    public Aircraft create(Aircraft aircraft) {
        if (aircraftRepository.existsByRegistrationNumber(aircraft.getRegistrationNumber())) {
            throw new IllegalArgumentException(
                    "Літак з реєстраційним номером " + aircraft.getRegistrationNumber() + " вже існує");
        }
        return aircraftRepository.save(aircraft);
    }

    @Transactional
    public Aircraft update(Long id, Aircraft updated) {
        return aircraftRepository.findById(id).map(a -> {
            a.setRegistrationNumber(updated.getRegistrationNumber());
            a.setModel(updated.getModel());
            a.setManufacturer(updated.getManufacturer());
            a.setYearOfManufacture(updated.getYearOfManufacture());
            a.setTotalSeats(updated.getTotalSeats());
            a.setMaxRangeKm(updated.getMaxRangeKm());
            a.setStatus(updated.getStatus());
            return aircraftRepository.save(a);
        }).orElseThrow(() ->
                new RuntimeException("Літак не знайдено: " + id));
    }

    @Transactional
    public void delete(Long id) {
        if (!aircraftRepository.existsById(id)) {
            throw new RuntimeException("Літак не знайдено: " + id);
        }
        aircraftRepository.deleteById(id);
    }

    @Transactional
    public Aircraft sendToMaintenance(Long aircraftId) {
        Aircraft aircraft = aircraftRepository.findById(aircraftId)
                .orElseThrow(() -> new RuntimeException("Літак не знайдено: " + aircraftId));
        if (aircraft.getStatus() == Aircraft.AircraftStatus.RETIRED) {
            throw new IllegalStateException(
                    "Не можна відправити списаний літак на обслуговування");
        }
        aircraft.setStatus(Aircraft.AircraftStatus.MAINTENANCE);
        return aircraftRepository.save(aircraft);
    }

    @Transactional
    public Aircraft activate(Long aircraftId) {
        Aircraft aircraft = aircraftRepository.findById(aircraftId)
                .orElseThrow(() -> new RuntimeException("Літак не знайдено: " + aircraftId));
        if (aircraft.getStatus() == Aircraft.AircraftStatus.RETIRED) {
            throw new IllegalStateException(
                    "Не можна активувати списаний літак");
        }
        aircraft.setStatus(Aircraft.AircraftStatus.ACTIVE);
        return aircraftRepository.save(aircraft);
    }

    @Transactional
    public Aircraft retire(Long aircraftId) {
        Aircraft aircraft = aircraftRepository.findById(aircraftId)
                .orElseThrow(() -> new RuntimeException("Літак не знайдено: " + aircraftId));
        aircraft.setStatus(Aircraft.AircraftStatus.RETIRED);
        return aircraftRepository.save(aircraft);
    }

    public List<Aircraft> getAircraftsNeedingMaintenance() {
        return aircraftRepository.findAll().stream()
                .filter(Aircraft::needsMaintenance)
                .toList();
    }
}