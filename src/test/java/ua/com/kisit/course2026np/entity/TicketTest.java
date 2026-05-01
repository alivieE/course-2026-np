package ua.com.kisit.course2026np.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Ticket Entity Tests")
class TicketTest {

    private Ticket ticket;

    @BeforeEach
    void setUp() {
        ticket = Ticket.builder()
                .ticketNumber("TK-1234567")
                .seatNumber("15B")
                .serviceClass(Ticket.ServiceClass.ECONOMY)
                .price(new BigDecimal("2500.00"))
                .status(Ticket.TicketStatus.RESERVED)
                .build();
    }

    @Nested
    @DisplayName("Status Check Tests")
    class StatusCheckTests {

        @Test
        @DisplayName("isConfirmed: повертає true для CONFIRMED")
        void isConfirmed_TrueForConfirmed() {
            ticket.setStatus(Ticket.TicketStatus.CONFIRMED);
            assertThat(ticket.isConfirmed()).isTrue();
        }

        @Test
        @DisplayName("isConfirmed: повертає false для RESERVED")
        void isConfirmed_FalseForReserved() {
            ticket.setStatus(Ticket.TicketStatus.RESERVED);
            assertThat(ticket.isConfirmed()).isFalse();
        }

        @Test
        @DisplayName("isCancelled: повертає true для CANCELLED")
        void isCancelled_TrueForCancelled() {
            ticket.setStatus(Ticket.TicketStatus.CANCELLED);
            assertThat(ticket.isCancelled()).isTrue();
        }

        @Test
        @DisplayName("isCancelled: повертає false для USED")
        void isCancelled_FalseForUsed() {
            ticket.setStatus(Ticket.TicketStatus.USED);
            assertThat(ticket.isCancelled()).isFalse();
        }

        @ParameterizedTest(name = "Статус {0}")
        @EnumSource(Ticket.TicketStatus.class)
        void everyStatusIsRecognizableAsItself(Ticket.TicketStatus status) {
            ticket.setStatus(status);
            assertThat(ticket.getStatus()).isEqualTo(status);
        }
    }

    @Nested
    @DisplayName("Cancel Tests")
    class CancelTests {

        @Test
        @DisplayName("cancel: заброньований квиток успішно скасовується")
        void cancel_Reserved_BecomesCancelled() {
            ticket.setStatus(Ticket.TicketStatus.RESERVED);
            ticket.cancel();
            assertThat(ticket.getStatus()).isEqualTo(Ticket.TicketStatus.CANCELLED);
        }

        @Test
        @DisplayName("cancel: підтверджений квиток успішно скасовується")
        void cancel_Confirmed_BecomesCancelled() {
            ticket.setStatus(Ticket.TicketStatus.CONFIRMED);
            ticket.cancel();
            assertThat(ticket.getStatus()).isEqualTo(Ticket.TicketStatus.CANCELLED);
        }

        @Test
        @DisplayName("cancel: використаний квиток НЕ можна скасувати → виняток")
        void cancel_Used_ThrowsException() {
            ticket.setStatus(Ticket.TicketStatus.USED);
            assertThatThrownBy(() -> ticket.cancel())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("використаний");
        }

        @Test
        @DisplayName("cancel: вже скасований квиток можна скасувати ще раз (ідемпотентно)")
        void cancel_AlreadyCancelled_StillCancelled() {
            ticket.setStatus(Ticket.TicketStatus.CANCELLED);
            ticket.cancel();
            assertThat(ticket.getStatus()).isEqualTo(Ticket.TicketStatus.CANCELLED);
        }
    }

    @Nested
    @DisplayName("Field Validation Tests")
    class FieldValidationTests {

        @Test
        @DisplayName("Поля квитка коректно зберігаються через builder")
        void builder_SetsAllFieldsCorrectly() {
            assertThat(ticket.getTicketNumber()).isEqualTo("TK-1234567");
            assertThat(ticket.getSeatNumber()).isEqualTo("15B");
            assertThat(ticket.getServiceClass()).isEqualTo(Ticket.ServiceClass.ECONOMY);
            assertThat(ticket.getPrice()).isEqualByComparingTo("2500.00");
            assertThat(ticket.getStatus()).isEqualTo(Ticket.TicketStatus.RESERVED);
        }
    }
}
