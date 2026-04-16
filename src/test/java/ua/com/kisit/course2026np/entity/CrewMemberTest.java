package ua.com.kisit.course2026np.entity;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CrewMember Entity Tests")
class CrewMemberTest {

    private CrewMember crewMember;

    @BeforeEach
    void setUp() {
        crewMember = CrewMember.builder()
                .firstName("John")
                .lastName("Doe")
                .role(CrewRole.PILOT)
                .experienceYears(15)
                .build();
    }

    @Test
    @DisplayName("Отримання повного імені")
    void getFullName_ReturnsFirstNameLastName() {
        assertEquals("John Doe", crewMember.getFullName());
    }

    @ParameterizedTest
    @CsvSource({
            "5, 3, true",
            "5, 5, true",
            "5, 7, false",
            "0, 1, false",
            "10, 10, true"
    })
    @DisplayName("Перевірка досвіду роботи")
    void isExperienced_ReturnsExpectedResult(int experience, int threshold, boolean expected) {
        crewMember.setExperienceYears(experience);
        assertEquals(expected, crewMember.isExperienced(threshold));
    }

    @Test
    @DisplayName("Перевірка ролі: пілот")
    void isPilot_ReturnsTrueForPilot() {
        crewMember.setRole(CrewRole.PILOT);
        assertTrue(crewMember.isPilot());
        assertFalse(crewMember.isNavigator());
        assertFalse(crewMember.isRadioOperator());
        assertFalse(crewMember.isStewardess());
    }

    @Test
    @DisplayName("Перевірка ролі: штурман")
    void isNavigator_ReturnsTrueForNavigator() {
        crewMember.setRole(CrewRole.NAVIGATOR);
        assertTrue(crewMember.isNavigator());
        assertFalse(crewMember.isPilot());
    }

    @Test
    @DisplayName("Перевірка ролі: радист")
    void isRadioOperator_ReturnsTrueForRadioOperator() {
        crewMember.setRole(CrewRole.RADIO_OPERATOR);
        assertTrue(crewMember.isRadioOperator());
    }

    @Test
    @DisplayName("Перевірка ролі: стюардеса")
    void isStewardess_ReturnsTrueForStewardess() {
        crewMember.setRole(CrewRole.STEWARDESS);
        assertTrue(crewMember.isStewardess());
    }

    @Test
    @DisplayName("Значення за замовчуванням не встановлено (всі поля обов'язкові)")
    void builder_RequiresAllMandatoryFields() {
        CrewMember member = CrewMember.builder()
                .firstName("Anna")
                .lastName("Ivanova")
                .role(CrewRole.STEWARDESS)
                .experienceYears(4)
                .build();
        assertAll(
                () -> assertEquals("Anna", member.getFirstName()),
                () -> assertEquals("Ivanova", member.getLastName()),
                () -> assertEquals(CrewRole.STEWARDESS, member.getRole()),
                () -> assertEquals(4, member.getExperienceYears())
        );
    }
}