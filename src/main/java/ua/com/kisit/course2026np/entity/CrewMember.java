package ua.com.kisit.course2026np.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "crew_members")
public class CrewMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "experience_years")
    private int experienceYears;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private CrewRole role;

    @ManyToOne
    @JoinColumn(name = "crew_id")
    private Crew crew;
}