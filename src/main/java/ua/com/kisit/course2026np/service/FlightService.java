package ua.com.kisit.course2026np.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.kisit.course2026np.entity.Flight;
import ua.com.kisit.course2026np.repository.CrewMemberRepository;
import ua.com.kisit.course2026np.repository.CrewRepository;
import ua.com.kisit.course2026np.repository.FlightRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class FlightService {

    private final FlightRepository flightRepository;
    private final TicketService ticketService;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;

    public FlightService(FlightRepository flightRepository,
                         TicketService ticketService,
                         CrewRepository crewRepository,
                         CrewMemberRepository crewMemberRepository) {
        this.flightRepository = flightRepository;
        this.ticketService = ticketService;
        this.crewRepository = crewRepository;
        this.crewMemberRepository = crewMemberRepository;
    }

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Optional<Flight> getById(Long id) {
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
    public Flight update(Long id, Flight updated) {
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
    public void delete(Long id) {
        if (!flightRepository.existsById(id)) {
            throw new RuntimeException("Рейс не знайдено: " + id);
        }
        crewMemberRepository.clearCurrentFlightByFlightId(id);
        ticketService.deleteByFlightId(id);
        if (crewRepository.existsByFlightId(id)) {
            crewRepository.deleteByFlightId(id);
        }
        flightRepository.deleteById(id);
    }
}