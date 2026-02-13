package ua.com.kisit.course2026np.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "crews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Crew {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "flight_id", nullable = false, unique = true)
    private Flight flight;

    @OneToMany(mappedBy = "crew", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CrewMember> members = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public void addMember(CrewMember member) {
        members.add(member);
        member.setCrew(this);
    }

    public void removeMember(CrewMember member) {
        members.remove(member);
        member.setCrew(null);
    }

    public boolean isFullyCrewed() {
        boolean hasPilot = members.stream().anyMatch(m -> m.getRole() == CrewRole.PILOT);
        boolean hasNavigator = members.stream().anyMatch(m -> m.getRole() == CrewRole.NAVIGATOR);
        boolean hasRadioOperator = members.stream().anyMatch(m -> m.getRole() == CrewRole.RADIO_OPERATOR);
        boolean hasStewardess = members.stream().anyMatch(m -> m.getRole() == CrewRole.STEWARDESS);
        return hasPilot && hasNavigator && hasRadioOperator && hasStewardess;
    }
}