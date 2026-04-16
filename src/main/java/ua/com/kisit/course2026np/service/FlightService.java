package ua.com.kisit.course2026np.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.kisit.course2026np.entity.Flight;
import ua.com.kisit.course2026np.repository.FlightRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }
    public Optional<Flight> getById(Integer id) {
        return flightRepository.findById(id);
    }
    public List<Flight> getPlannedFlights() {
        return flightRepository.findByStatusOrderByDeparture("PLANNED");
    }
    public List<Flight> getByStatus(String status) {
        return flightRepository.findByStatus(status);
    }
    @Transactional
    public Flight create(Flight flight) {
        if (flightRepository.existsByFlightNumber(flight.getFlightNumber())) {
            throw new IllegalArgumentException(
                    "Рейс з номером " + flight.getFlightNumber() + " вже існує");
        }
        return flightRepository.save(flight);
    }
    @Transactional
    public Flight update(Integer id, Flight updated) {
        return flightRepository.findById(id).map(f -> {
            f.setFlightNumber(updated.getFlightNumber());
            f.setDepartureCity(updated.getDepartureCity());
            f.setArrivalCity(updated.getArrivalCity());
            f.setDepartureTime(updated.getDepartureTime());
            f.setArrivalTime(updated.getArrivalTime());
            f.setStatus(updated.getStatus());
            return flightRepository.save(f);
        }).orElseThrow(() ->
                new RuntimeException("Рейс не знайдено: " + id));
    }
    @Transactional
    public void delete(Integer id) {
        if (!flightRepository.existsById(id)) {
            throw new RuntimeException("Рейс не знайдено: " + id);
        }
        flightRepository.deleteById(id);
    }
}