package ua.com.kisit.course2026np.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets", indexes = {
        @Index(name = "idx_ticket_number", columnList = "ticket_number"),
        @Index(name = "idx_passenger_id", columnList = "passenger_id"),
        @Index(name = "idx_flight_id", columnList = "flight_id"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"passenger", "flight"})
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Pattern(regexp = "^TK-[0-9]{7}$",
            message = "Номер квитка має формат TK-XXXXXXX")
    @Column(name = "ticket_number", nullable = false, unique = true, length = 15)
    private String ticketNumber;

    @NotBlank
    @Pattern(regexp = "^[0-9]{1,3}[A-F]$",
            message = "Невірний формат номера сидіння (приклад: 12A)")
    @Column(name = "seat_number", nullable = false, length = 4)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_class", nullable = false, length = 20)
    private ServiceClass serviceClass;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Ціна має бути додатною")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TicketStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public boolean isConfirmed() {
        return status == TicketStatus.CONFIRMED;
    }

    public boolean isCancelled() {
        return status == TicketStatus.CANCELLED;
    }

    public void cancel() {
        if (status == TicketStatus.USED) {
            throw new IllegalStateException(
                    "Не можна скасувати вже використаний квиток");
        }
        this.status = TicketStatus.CANCELLED;
    }

    public enum ServiceClass {
        ECONOMY,    // Економ-клас
        BUSINESS,   // Бізнес-клас
        FIRST       // Перший клас
    }

    public enum TicketStatus {
        RESERVED,   // Заброньовано (очікує оплати)
        CONFIRMED,  // Підтверджено (оплачено)
        CANCELLED,  // Скасовано
        USED        // Використано (пасажир здійснив посадку)
    }
}