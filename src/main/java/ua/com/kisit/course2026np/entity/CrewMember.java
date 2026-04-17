package ua.com.kisit.course2026np.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "crew_member", indexes = {
        @Index(name = "idx_crew_id", columnList = "crew_id"),
        @Index(name = "idx_role", columnList = "role")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrewMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Positive
    @Max(60)
    @Column(name = "experience_years", nullable = false)
    private int experienceYears;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private CrewRole role;

    @ManyToOne
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;

    @ManyToOne
    @JoinColumn(name = "current_flight_id")
    private Flight currentFlight;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isExperienced(int threshold) {
        return this.experienceYears >= threshold;
    }

    // Методи для зручної перевірки ролі
    public boolean isPilot() {
        return role == CrewRole.PILOT;
    }

    public boolean isNavigator() {
        return role == CrewRole.NAVIGATOR;
    }

    public boolean isRadioOperator() {
        return role == CrewRole.RADIO_OPERATOR;
    }

    public boolean isStewardess() {
        return role == CrewRole.STEWARDESS;
    }
}