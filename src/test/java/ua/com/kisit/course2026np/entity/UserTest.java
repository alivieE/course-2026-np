package ua.com.kisit.course2026np.entity;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User Entity Tests")
class UserTest {

    private User user;
    private Flight flight;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .firstName("Ivan")
                .lastName("Petrov")
                .email("ivan@example.com")
                .password("password123")
                .role(UserRole.DISPATCHER)
                .build();

        flight = Flight.builder()
                .flightNumber("PS202")
                .departureCity("Kyiv")
                .arrivalCity("Odesa")
                .departureTime(java.time.LocalDateTime.now().plusDays(2))
                .arrivalTime(java.time.LocalDateTime.now().plusDays(2).plusHours(1))
                .build();
    }

    @Test
    @DisplayName("Додавання рейсу до користувача встановлює двосторонній зв'язок")
    void addFlight_SetsBidirectionalLink() {
        user.addFlight(flight);

        assertThat(user.getFlights()).contains(flight);
        assertThat(flight.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("Видалення рейсу з користувача розриває зв'язок")
    void removeFlight_RemovesBidirectionalLink() {
        user.addFlight(flight);
        user.removeFlight(flight);

        assertThat(user.getFlights()).doesNotContain(flight);
        assertThat(flight.getUser()).isNull();
    }

    @Test
    @DisplayName("Роль за замовчуванням - DISPATCHER")
    void defaultRole_ShouldBeDispatcher() {
        User newUser = User.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("test123")
                .build();
        assertEquals(UserRole.DISPATCHER, newUser.getRole());
    }

    @Test
    @DisplayName("Статус активності за замовчуванням - true")
    void defaultIsActive_ShouldBeTrue() {
        User newUser = User.builder()
                .firstName("Test")
                .lastName("User")
                .email("test2@example.com")
                .password("test123")
                .build();
        assertTrue(newUser.getIsActive());
    }
}