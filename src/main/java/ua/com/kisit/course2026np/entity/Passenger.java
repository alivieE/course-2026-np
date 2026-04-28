package ua.com.kisit.course2026np.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "passengers", indexes = {
        @Index(name = "idx_passport_number", columnList = "passport_number"),
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_last_name", columnList = "last_name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"tickets"})
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @NotBlank
    @Size(min = 6, max = 20)
    @Pattern(regexp = "^[A-Z0-9]+$",
            message = "Серія та номер паспорта мають містити лише великі літери та цифри")
    @Column(name = "passport_number", nullable = false, unique = true, length = 20)
    private String passportNumber;

    @Email
    @NotBlank
    @Size(max = 100)
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$",
            message = "Невірний формат номера телефону")
    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @OneToMany(mappedBy = "passenger", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public String getFullName() {
        return lastName + " " + firstName;
    }

    public Integer getAge() {
        if (dateOfBirth == null) return null;
        return java.time.Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public boolean isAdult() {
        Integer age = getAge();
        return age != null && age >= 18;
    }

    public void addTicket(Ticket ticket) {
        if (!tickets.contains(ticket)) {
            tickets.add(ticket);
            ticket.setPassenger(this);
        }
    }

    public void removeTicket(Ticket ticket) {
        tickets.remove(ticket);
        ticket.setPassenger(null);
    }

    public enum Gender {
        MALE, FEMALE, OTHER
    }
}