package ua.com.kisit.course2026np.entity;

import org.junit.jupiter.api.*;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Flight Entity Tests")
class FlightTest {

    private Flight flight;

    @BeforeEach
    void setUp() {
        flight = Flight.builder()
                .flightNumber("PS101")
                .departureCity("Kyiv")
                .arrivalCity("London")
                .departureTime(LocalDateTime.now().plusDays(5))
                .arrivalTime(LocalDateTime.now().plusDays(5).plusHours(3))
                .status(FlightStatus.PLANNED)
                .user(User.builder()
                        .firstName("Ivan")
                        .lastName("Petrov")
                        .email("ivan@example.com")
                        .password("pass")
                        .build())
                .build();
    }

    @Nested
    @DisplayName("Flight Status Transition Tests")
    class StatusTransitionTests {

        @Test
        @DisplayName("PLANNED → IN_PROGRESS — допустимий перехід")
        void changeStatus_PlannedToInProgress_Success() {
            flight.changeStatus(FlightStatus.IN_PROGRESS);
            assertEquals(FlightStatus.IN_PROGRESS, flight.getStatus());
        }

        @Test
        @DisplayName("IN_PROGRESS → COMPLETED — допустимий перехід")
        void changeStatus_InProgressToCompleted_Success() {
            flight.setStatus(FlightStatus.IN_PROGRESS);
            flight.changeStatus(FlightStatus.COMPLETED);
            assertEquals(FlightStatus.COMPLETED, flight.getStatus());
        }

        @Test
        @DisplayName("IN_PROGRESS → CANCELLED — допустимий перехід")
        void changeStatus_InProgressToCancelled_Success() {
            flight.setStatus(FlightStatus.IN_PROGRESS);
            flight.changeStatus(FlightStatus.CANCELLED);
            assertEquals(FlightStatus.CANCELLED, flight.getStatus());
        }

        @Test
        @DisplayName("COMPLETED → CANCELLED — НЕ допустимий перехід")
        void changeStatus_CompletedToCancelled_ThrowsException() {
            flight.setStatus(FlightStatus.COMPLETED);
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> flight.changeStatus(FlightStatus.CANCELLED)
            );
            assertThat(exception.getMessage()).contains("Неможливо змінити статус після завершення");
        }

        @Test
        @DisplayName("CANCELLED → будь-що — НЕ допустимий перехід")
        void changeStatus_CancelledToAny_ThrowsException() {
            flight.setStatus(FlightStatus.CANCELLED);
            assertThrows(IllegalStateException.class,
                    () -> flight.changeStatus(FlightStatus.PLANNED));
            assertThrows(IllegalStateException.class,
                    () -> flight.changeStatus(FlightStatus.IN_PROGRESS));
        }

        @Test
        @DisplayName("Зміна на той самий статус — допустима (нічого не змінюється)")
        void changeStatus_SameStatus_DoesNothing() {
            flight.setStatus(FlightStatus.PLANNED);
            flight.changeStatus(FlightStatus.PLANNED);
            assertEquals(FlightStatus.PLANNED, flight.getStatus());
        }
    }

    @Nested
    @DisplayName("Flight Business Logic Tests")
    class FlightBusinessLogicTests {

        @Test
        @DisplayName("Розрахунок тривалості польоту")
        void getDuration_ReturnsCorrectMinutes() {
            LocalDateTime depart = LocalDateTime.of(2026, 3, 25, 10, 0);
            LocalDateTime arrive = LocalDateTime.of(2026, 3, 25, 13, 30);
            flight.setDepartureTime(depart);
            flight.setArrivalTime(arrive);

            assertEquals(210, flight.getDuration());
        }

        @Test
        @DisplayName("Перевірка затримки: час вильоту в минулому = затримка")
        void isDelayed_DepartureInPast_ReturnsTrue() {
            flight.setDepartureTime(LocalDateTime.now().minusHours(1));
            assertTrue(flight.isDelayed());
        }

        @Test
        @DisplayName("Перевірка затримки: час вильоту в майбутньому = не затримка")
        void isDelayed_DepartureInFuture_ReturnsFalse() {
            flight.setDepartureTime(LocalDateTime.now().plusHours(2));
            assertFalse(flight.isDelayed());
        }

        @Test
        @DisplayName("Автоматичне оновлення статусу при затримці")
        void checkAndUpdateDelay_WhenDelayedAndPlanned_SetsCancelled() {
            flight.setDepartureTime(LocalDateTime.now().minusMinutes(30));
            flight.setStatus(FlightStatus.PLANNED);

            flight.checkAndUpdateDelay();

            assertEquals(FlightStatus.CANCELLED, flight.getStatus());
        }
    }
}