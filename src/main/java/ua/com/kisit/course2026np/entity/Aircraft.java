package ua.com.kisit.course2026np.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "aircrafts", indexes = {
        @Index(name = "idx_registration_number", columnList = "registration_number"),
        @Index(name = "idx_model", columnList = "model"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Aircraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{2}-[0-9A-Z]{4,6}$",
            message = "Невірний формат реєстраційного номера (приклад: UR-12345)")
    @Column(name = "registration_number", nullable = false, unique = true, length = 12)
    private String registrationNumber;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(name = "model", nullable = false, length = 50)
    private String model;

    @NotBlank
    @Size(max = 50)
    @Column(name = "manufacturer", nullable = false, length = 50)
    private String manufacturer;

    @Min(1950)
    @Column(name = "year_of_manufacture")
    private Integer yearOfManufacture;

    @Min(1)
    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Min(0)
    @Column(name = "max_range_km")
    private Integer maxRangeKm;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AircraftStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public boolean isReadyForFlight() {
        return status == AircraftStatus.ACTIVE;
    }

    public Integer getAge() {
        if (yearOfManufacture == null) return null;
        return java.time.LocalDate.now().getYear() - yearOfManufacture;
    }

    public boolean needsMaintenance() {
        Integer age = getAge();
        return status == AircraftStatus.MAINTENANCE ||
                (age != null && age > 25);
    }

    public enum AircraftStatus {
        ACTIVE,           // У робочому стані
        MAINTENANCE,      // На технічному обслуговуванні
        OUT_OF_SERVICE,   // Виведено з експлуатації
        RETIRED           // Списано
    }
}