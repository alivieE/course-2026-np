package ua.com.kisit.course2026np.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Passenger Entity Tests")
class PassengerTest {

    private Passenger passenger;

    @BeforeEach
    void setUp() {
        passenger = Passenger.builder()
                .firstName("Іван")
                .lastName("Петренко")
                .passportNumber("AB123456")
                .email("ivan.petrenko@example.com")
                .phone("+380501234567")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .gender(Passenger.Gender.MALE)
                .build();
    }

    @Nested
    @DisplayName("Basic Field Tests")
    class BasicFieldTests {

        @Test
        @DisplayName("getFullName: повертає 'Прізвище Ім'я'")
        void getFullName_ReturnsLastNameFirstName() {
            assertThat(passenger.getFullName()).isEqualTo("Петренко Іван");
        }

        @Test
        @DisplayName("getAge: коректно обчислює вік за датою народження")
        void getAge_CalculatesCorrectAge() {
            int expectedAge = LocalDate.now().getYear() - 1990;
            // Якщо в поточному році ще не було дня народження — віднімаємо 1
            if (LocalDate.now().getMonthValue() < 5 ||
                (LocalDate.now().getMonthValue() == 5 && LocalDate.now().getDayOfMonth() < 15)) {
                expectedAge--;
            }
            assertThat(passenger.getAge()).isEqualTo(expectedAge);
        }

        @Test
        @DisplayName("getAge: повертає null якщо дата народження не задана")
        void getAge_ReturnsNull_WhenDateOfBirthIsNull() {
            passenger.setDateOfBirth(null);
            assertThat(passenger.getAge()).isNull();
        }
    }

    @Nested
    @DisplayName("Adult Status Tests")
    class AdultStatusTests {

        @Test
        @DisplayName("isAdult: дорослий пасажир (1990 рік)")
        void isAdult_AdultPassenger_ReturnsTrue() {
            assertThat(passenger.isAdult()).isTrue();
        }

        @Test
        @DisplayName("isAdult: неповнолітній пасажир (10 років)")
        void isAdult_MinorPassenger_ReturnsFalse() {
            passenger.setDateOfBirth(LocalDate.now().minusYears(10));
            assertThat(passenger.isAdult()).isFalse();
        }

        @Test
        @DisplayName("isAdult: пасажир без дати народження → false")
        void isAdult_NoDateOfBirth_ReturnsFalse() {
            passenger.setDateOfBirth(null);
            assertThat(passenger.isAdult()).isFalse();
        }

        @ParameterizedTest(name = "Вік {0} років → дорослий: {1}")
        @CsvSource({
                "5, false",
                "17, false",
                "18, true",
                "25, true",
                "65, true"
        })
        void isAdult_ParameterizedAgeCheck(int age, boolean expectedAdult) {
            passenger.setDateOfBirth(LocalDate.now().minusYears(age));
            assertThat(passenger.isAdult()).isEqualTo(expectedAdult);
        }
    }

    @Nested
    @DisplayName("Ticket Management Tests")
    class TicketManagementTests {

        @Test
        @DisplayName("addTicket: додає квиток і встановлює двосторонній зв'язок")
        void addTicket_EstablishesBidirectionalLink() {
            Ticket ticket = Ticket.builder()
                    .ticketNumber("TK-1234567")
                    .seatNumber("12A")
                    .build();

            passenger.addTicket(ticket);

            assertThat(passenger.getTickets()).hasSize(1).contains(ticket);
            assertThat(ticket.getPassenger()).isEqualTo(passenger);
        }

        @Test
        @DisplayName("addTicket: повторне додавання НЕ створює дубль")
        void addTicket_DoesNotAddDuplicate() {
            Ticket ticket = Ticket.builder().ticketNumber("TK-1234567").build();
            passenger.addTicket(ticket);
            passenger.addTicket(ticket);
            assertThat(passenger.getTickets()).hasSize(1);
        }

        @Test
        @DisplayName("removeTicket: видаляє квиток і розриває зв'язок")
        void removeTicket_BreaksBidirectionalLink() {
            Ticket ticket = Ticket.builder().ticketNumber("TK-1234567").build();
            passenger.addTicket(ticket);

            passenger.removeTicket(ticket);

            assertThat(passenger.getTickets()).isEmpty();
            assertThat(ticket.getPassenger()).isNull();
        }
    }
}
