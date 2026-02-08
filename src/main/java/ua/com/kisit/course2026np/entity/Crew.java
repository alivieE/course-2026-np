package ua.com.kisit.course2026np.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "crews")
public class Crew {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "crew", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CrewMember> members;

    @OneToOne(mappedBy = "crew")
    private Flight flight;

    public int getTotalMembers() {
        return members != null ? members.size() : 0;
    }
}