package ua.com.kisit.course2026np.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "crew_members")
public class CrewMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private int experienceYears;

    @Enumerated(EnumType.STRING)
    private CrewRole role;

    @ManyToOne
    @JoinColumn(name = "crew_id")
    private Crew crew;

    public CrewMember() {
    }

    // getters and setters
}
