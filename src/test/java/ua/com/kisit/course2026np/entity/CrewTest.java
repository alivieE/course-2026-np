package ua.com.kisit.course2026np.entity;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Crew Entity Tests")
class CrewTest {

    private Crew crew;
    private CrewMember pilot;
    private CrewMember navigator;
    private CrewMember radioOperator;
    private CrewMember stewardess;

    @BeforeEach
    void setUp() {
        crew = Crew.builder()
                .flight(new Flight()) // для коректності, але не впливає на тести
                .build();

        pilot = CrewMember.builder()
                .role(CrewRole.PILOT)
                .firstName("John")
                .lastName("Smith")
                .experienceYears(12)
                .build();

        navigator = CrewMember.builder()
                .role(CrewRole.NAVIGATOR)
                .firstName("Alice")
                .lastName("Johnson")
                .experienceYears(8)
                .build();

        radioOperator = CrewMember.builder()
                .role(CrewRole.RADIO_OPERATOR)
                .firstName("Bob")
                .lastName("Williams")
                .experienceYears(5)
                .build();

        stewardess = CrewMember.builder()
                .role(CrewRole.STEWARDESS)
                .firstName("Emma")
                .lastName("Brown")
                .experienceYears(3)
                .build();
    }

    @Nested
    @DisplayName("Crew Composition Tests")
    class CrewCompositionTests {

        @Test
        @DisplayName("Бригада з усіма 4 ролями вважається укомплектованою")
        void fullyCrewed_AllRolesPresent_ReturnsTrue() {
            crew.addMember(pilot);
            crew.addMember(navigator);
            crew.addMember(radioOperator);
            crew.addMember(stewardess);

            assertTrue(crew.isFullyCrewed());
            assertThat(crew.getMembers()).hasSize(4);
        }

        @Test
        @DisplayName("Бригада без пілота НЕ укомплектована")
        void fullyCrewed_NoPilot_ReturnsFalse() {
            crew.addMember(navigator);
            crew.addMember(radioOperator);
            crew.addMember(stewardess);

            assertFalse(crew.isFullyCrewed());
        }

        @Test
        @DisplayName("Бригада без штурмана НЕ укомплектована")
        void fullyCrewed_NoNavigator_ReturnsFalse() {
            crew.addMember(pilot);
            crew.addMember(radioOperator);
            crew.addMember(stewardess);

            assertFalse(crew.isFullyCrewed());
        }

        @Test
        @DisplayName("Бригада без радиста НЕ укомплектована")
        void fullyCrewed_NoRadioOperator_ReturnsFalse() {
            crew.addMember(pilot);
            crew.addMember(navigator);
            crew.addMember(stewardess);

            assertFalse(crew.isFullyCrewed());
        }

        @Test
        @DisplayName("Бригада без стюардеси НЕ укомплектована")
        void fullyCrewed_NoStewardess_ReturnsFalse() {
            crew.addMember(pilot);
            crew.addMember(navigator);
            crew.addMember(radioOperator);

            assertFalse(crew.isFullyCrewed());
        }

        @Test
        @DisplayName("КРИТИЧНИЙ БАГ: Бригада з 4 стюардесами НЕ укомплектована (виправлено)")
        void fullyCrewed_OnlyStewardesses_ReturnsFalse() {
            for (int i = 0; i < 4; i++) {
                CrewMember s = CrewMember.builder()
                        .role(CrewRole.STEWARDESS)
                        .firstName("Steward" + i)
                        .lastName("Test")
                        .experienceYears(2)
                        .build();
                crew.addMember(s);
            }

            assertFalse(crew.isFullyCrewed());
            assertThat(crew.getMembers()).hasSize(4);
        }

        @Test
        @DisplayName("Порожня бригада НЕ укомплектована")
        void fullyCrewed_EmptyCrew_ReturnsFalse() {
            assertFalse(crew.isFullyCrewed());
        }
    }

    @Nested
    @DisplayName("Crew Member Management Tests")
    class CrewMemberManagementTests {

        @Test
        @DisplayName("Додавання члена бригади встановлює двосторонній зв'язок")
        void addMember_SetsBidirectionalLink() {
            crew.addMember(pilot);

            assertThat(crew.getMembers()).contains(pilot);
            assertThat(pilot.getCrew()).isEqualTo(crew);
        }

        @Test
        @DisplayName("Видалення члена бригади розриває зв'язок")
        void removeMember_RemovesBidirectionalLink() {
            crew.addMember(pilot);
            crew.removeMember(pilot);

            assertThat(crew.getMembers()).doesNotContain(pilot);
            assertThat(pilot.getCrew()).isNull();
        }

        @Test
        @DisplayName("Додавання одного члена двічі не створює дублікат")
        void addMember_Duplicate_NotAddedTwice() {
            crew.addMember(pilot);
            crew.addMember(pilot);

            assertThat(crew.getMembers()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Parameterized Crew Tests")
    class ParameterizedCrewTests {

        @ParameterizedTest
        @EnumSource(value = CrewRole.class, names = {"PILOT", "NAVIGATOR", "RADIO_OPERATOR", "STEWARDESS"})
        @DisplayName("Бригада з однією роллю НЕ укомплектована")
        void fullyCrewed_SingleRole_ReturnsFalse(CrewRole singleRole) {
            CrewMember member = CrewMember.builder()
                    .role(singleRole)
                    .firstName("Test")
                    .lastName("User")
                    .experienceYears(5)
                    .build();
            crew.addMember(member);
            assertFalse(crew.isFullyCrewed());
        }
    }
}