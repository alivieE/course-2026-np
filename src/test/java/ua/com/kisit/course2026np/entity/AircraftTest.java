package ua.com.kisit.course2026np.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Aircraft Entity Tests")
class AircraftTest {

    private Aircraft aircraft;

    @BeforeEach
    void setUp() {
        aircraft = Aircraft.builder()
                .registrationNumber("UR-12345")
                .model("Boeing 737")
                .manufacturer("Boeing")
                .yearOfManufacture(2015)
                .totalSeats(189)
                .maxRangeKm(5765)
                .status(Aircraft.AircraftStatus.ACTIVE)
                .build();
    }

    @Nested
    @DisplayName("Ready For Flight Tests")
    class ReadyForFlightTests {

        @Test
        @DisplayName("isReadyForFlight: ACTIVE → true")
        void isReadyForFlight_Active_True() {
            aircraft.setStatus(Aircraft.AircraftStatus.ACTIVE);
            assertThat(aircraft.isReadyForFlight()).isTrue();
        }

        @Test
        @DisplayName("isReadyForFlight: MAINTENANCE → false")
        void isReadyForFlight_Maintenance_False() {
            aircraft.setStatus(Aircraft.AircraftStatus.MAINTENANCE);
            assertThat(aircraft.isReadyForFlight()).isFalse();
        }

        @Test
        @DisplayName("isReadyForFlight: OUT_OF_SERVICE → false")
        void isReadyForFlight_OutOfService_False() {
            aircraft.setStatus(Aircraft.AircraftStatus.OUT_OF_SERVICE);
            assertThat(aircraft.isReadyForFlight()).isFalse();
        }

        @Test
        @DisplayName("isReadyForFlight: RETIRED → false")
        void isReadyForFlight_Retired_False() {
            aircraft.setStatus(Aircraft.AircraftStatus.RETIRED);
            assertThat(aircraft.isReadyForFlight()).isFalse();
        }
    }

    @Nested
    @DisplayName("Age Calculation Tests")
    class AgeCalculationTests {

        @Test
        @DisplayName("getAge: коректно обчислює вік за роком випуску")
        void getAge_CalculatesCorrectAge() {
            int currentYear = LocalDate.now().getYear();
            int expectedAge = currentYear - 2015;
            assertThat(aircraft.getAge()).isEqualTo(expectedAge);
        }

        @Test
        @DisplayName("getAge: повертає null якщо рік випуску не задано")
        void getAge_ReturnsNull_WhenYearIsNull() {
            aircraft.setYearOfManufacture(null);
            assertThat(aircraft.getAge()).isNull();
        }
    }

    @Nested
    @DisplayName("Maintenance Tests")
    class MaintenanceTests {

        @Test
        @DisplayName("needsMaintenance: статус MAINTENANCE → true")
        void needsMaintenance_StatusMaintenance_True() {
            aircraft.setStatus(Aircraft.AircraftStatus.MAINTENANCE);
            aircraft.setYearOfManufacture(LocalDate.now().getYear());
            assertThat(aircraft.needsMaintenance()).isTrue();
        }

        @Test
        @DisplayName("needsMaintenance: новий ACTIVE літак → false")
        void needsMaintenance_NewActive_False() {
            aircraft.setStatus(Aircraft.AircraftStatus.ACTIVE);
            aircraft.setYearOfManufacture(LocalDate.now().getYear() - 5);
            assertThat(aircraft.needsMaintenance()).isFalse();
        }

        @Test
        @DisplayName("needsMaintenance: старий ACTIVE літак (>25 років) → true")
        void needsMaintenance_OldActive_True() {
            aircraft.setStatus(Aircraft.AircraftStatus.ACTIVE);
            aircraft.setYearOfManufacture(LocalDate.now().getYear() - 30);
            assertThat(aircraft.needsMaintenance()).isTrue();
        }

        @Test
        @DisplayName("needsMaintenance: вік не заданий і статус ACTIVE → false")
        void needsMaintenance_NoYearActive_False() {
            aircraft.setStatus(Aircraft.AircraftStatus.ACTIVE);
            aircraft.setYearOfManufacture(null);
            assertThat(aircraft.needsMaintenance()).isFalse();
        }

        @ParameterizedTest(name = "Вік {0} років, статус {1} → потребує обслуговування: {2}")
        @CsvSource({
                "5, ACTIVE, false",
                "20, ACTIVE, false",
                "26, ACTIVE, true",
                "30, ACTIVE, true",
                "5, MAINTENANCE, true",
                "5, OUT_OF_SERVICE, false"
        })
        void needsMaintenance_ParameterizedCheck(int age, Aircraft.AircraftStatus status, boolean expected) {
            aircraft.setYearOfManufacture(LocalDate.now().getYear() - age);
            aircraft.setStatus(status);
            assertThat(aircraft.needsMaintenance()).isEqualTo(expected);
        }
    }
}
