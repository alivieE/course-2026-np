package ua.com.kisit.course2026np.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.Duration;

@Entity
@Table(name = "flights", indexes = {
        @Index(name = "idx_flight_number", columnList = "flight_number"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_departure_time", columnList = "departure_time"),
        @Index(name = "idx_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "flight_number", nullable = false, unique = true)
    private String flightNumber;

    @NotBlank
    @Column(name = "departure_city", nullable = false)
    private String departureCity;

    @NotBlank
    @Column(name = "arrival_city", nullable = false)
    private String arrivalCity;

    @Future
    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Future
    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'PLANNED'")
    @Builder.Default
    private FlightStatus status = FlightStatus.PLANNED;

    // Зв'язок Many-to-One з User (користувач, який створив рейс)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Зв'язок One-to-One з Crew (один рейс – одна бригада)
    @OneToOne(mappedBy = "flight", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private Crew brigade;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    // ============ БІЗНЕС-МЕТОДИ ============

    /**
     * Зміна статусу рейсу з перевіркою допустимості переходу.
     * Завершені та скасовані рейси не можна змінювати.
     */
    public void changeStatus(FlightStatus newStatus) {
        if (this.status == FlightStatus.COMPLETED || this.status == FlightStatus.CANCELLED) {
            throw new IllegalStateException("Неможливо змінити статус після завершення або скасування рейсу");
        }
        this.status = newStatus;
    }

    /**
     * Тривалість польоту в хвилинах.
     */
    public long getDuration() {
        return Duration.between(departureTime, arrivalTime).toMinutes();
    }

    /**
     * Перевірка, чи рейс затримується (поточний час > час вильоту).
     */
    public boolean isDelayed() {
        return LocalDateTime.now().isAfter(departureTime);
    }

    /**
     * Автоматичне оновлення статусу на DELAYED, якщо рейс ще не вилетів,
     * але час вильоту вже минув.
     */
    public void checkAndUpdateDelay() {
        if (isDelayed() && this.status == FlightStatus.PLANNED) {
            this.status = FlightStatus.CANCELLED; // або можна розширити enum, додавши DELAYED
        }
    }
}