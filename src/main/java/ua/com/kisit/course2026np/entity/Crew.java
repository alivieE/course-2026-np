package ua.com.kisit.course2026np.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "crews")
public class Crew {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "crew", cascade = CascadeType.ALL)
    private List<CrewMember> members;

    @OneToOne(mappedBy = "crew")
    private Flight flight;

    public Crew() {
    }

    // getters and setters
}
