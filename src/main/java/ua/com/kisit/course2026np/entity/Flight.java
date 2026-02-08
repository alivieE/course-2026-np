package ua.com.kisit.course2026np.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "flights")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String flightNumber;
    private String departureCity;
    private String arrivalCity;

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    @Enumerated(EnumType.STRING)
    private FlightStatus status;

    @OneToOne
    @JoinColumn(name = "crew_id")
    private Crew crew;

    public Flight() {
    }

    // getters and setters
}
