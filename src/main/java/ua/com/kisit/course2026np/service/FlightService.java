package ua.com.kisit.course2026np.service;

import ua.com.kisit.course2026np.entity.Flight;
import ua.com.kisit.course2026np.entity.FlightStatus;
import ua.com.kisit.course2026np.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {
    private final FlightRepository flightRepository;

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Flight getFlightById(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
    }

    public Flight saveFlight(Flight flight) {
        return flightRepository.save(flight);
    }

    public void deleteFlight(Long id) {
        flightRepository.deleteById(id);
    }

    public List<Flight> getFlightsByStatus(FlightStatus status) {
        return flightRepository.findByStatus(status);
    }

    @Transactional
    public Flight updateFlightStatus(Long id, FlightStatus newStatus) {
        Flight flight = getFlightById(id);
        flight.setStatus(newStatus);
        return flightRepository.save(flight);
    }
}