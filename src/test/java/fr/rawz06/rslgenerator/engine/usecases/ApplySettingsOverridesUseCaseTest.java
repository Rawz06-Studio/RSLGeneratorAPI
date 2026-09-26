package fr.rawz06.rslgenerator.engine.usecases;

import fr.rawz06.rslgenerator.engine.domain.entities.Preset;
import fr.rawz06.rslgenerator.engine.domain.entities.SettingsFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApplySettingsOverridesUseCaseTest {

    private ApplySettingsOverridesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ApplySettingsOverridesUseCase();
    }

    @Nested
    @DisplayName("Default overrides")
    class DefaultOverridesTests {

        @Test
        @DisplayName("Sets show_seed_info to true")
        void setsShowSeedInfoTrue() {
            SettingsFile base = new SettingsFile(Map.of());
            SettingsFile result = useCase.apply(base, Preset.RSL);

            assertThat(result.settings()).containsEntry("show_seed_info", true);
        }

        @Test
        @DisplayName("Sets create_spoiler to true")
        void setsCreateSpoilerTrue() {
            SettingsFile base = new SettingsFile(Map.of());
            SettingsFile result = useCase.apply(base, Preset.RSL);

            assertThat(result.settings()).containsEntry("create_spoiler", true);
        }

        @Test
        @DisplayName("Sets password_lock to false")
        void setsPasswordLockFalse() {
            SettingsFile base = new SettingsFile(Map.of());
            SettingsFile result = useCase.apply(base, Preset.RSL);

            assertThat(result.settings()).containsEntry("password_lock", false);
        }

        @Test
        @DisplayName("Preserves base settings")
        void preservesBaseSettings() {
            SettingsFile base = new SettingsFile(Map.of("open_forest", "closed"));
            SettingsFile result = useCase.apply(base, Preset.RSL);

            assertThat(result.settings()).containsEntry("open_forest", "closed");
        }

        @Test
        @DisplayName("Overrides take precedence over base values")
        void overridesTakePrecedence() {
            SettingsFile base = new SettingsFile(Map.of("show_seed_info", false));
            SettingsFile result = useCase.apply(base, Preset.RSL);

            assertThat(result.settings()).containsEntry("show_seed_info", true);
        }
    }

    @Nested
    @DisplayName("ROT preset behavior")
    class RotPresetTests {

        @Test
        @DisplayName("ROT forces adult trade sequence")
        void rotForcesAdultTradeSequence() {
            SettingsFile base = new SettingsFile(Map.of());
            SettingsFile result = useCase.apply(base, Preset.ROT);

            assertThat((String[]) result.settings().get("adult_trade_start"))
                    .containsExactly("Prescription", "Eyeball Frog", "Eyedrops", "Claim Check");
        }

        @Test
        @DisplayName("Non-ROT presets do not set adult_trade_start")
        void nonRotDoesNotSetTradeSequence() {
            SettingsFile base = new SettingsFile(Map.of());
            SettingsFile result = useCase.apply(base, Preset.RSL);

            assertThat(result.settings()).doesNotContainKey("adult_trade_start");
        }
    }

    @Nested
    @DisplayName("Immutability guarantees")
    class ImmutabilityTests {

        @Test
        @DisplayName("Does not mutate input SettingsFile")
        void doesNotMutateInput() {
            Map<String, Object> baseMap = new java.util.HashMap<>(Map.of("open_forest", "closed"));
            SettingsFile base = new SettingsFile(baseMap);

            useCase.apply(base, Preset.RSL);

            // Input map must remain unchanged
            assertThat(baseMap).containsOnlyKeys("open_forest");
            assertThat(base.settings()).containsOnlyKeys("open_forest");
        }

        @Test
        @DisplayName("Result is immutable due to Map.copyOf in SettingsFile")
        void resultIsImmutable() {
            SettingsFile base = new SettingsFile(Map.of());
            SettingsFile result = useCase.apply(base, Preset.RSL);

            assertThatThrownBy(() -> result.settings().put("key", "value"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("Multiple calls produce independent results")
        void multipleCallsProduceIndependentResults() {
            SettingsFile base1 = new SettingsFile(Map.of("open_forest", "closed"));
            SettingsFile base2 = new SettingsFile(Map.of("logic_rules", "glitchless"));

            SettingsFile result1 = useCase.apply(base1, Preset.RSL);
            SettingsFile result2 = useCase.apply(base2, Preset.RSL);

            // Both should have their respective base settings
            assertThat(result1.settings()).containsEntry("open_forest", "closed");
            assertThat(result2.settings()).containsEntry("logic_rules", "glitchless");

            // And both should have overrides
            assertThat(result1.settings()).containsEntry("show_seed_info", true);
            assertThat(result2.settings()).containsEntry("show_seed_info", true);
        }
    }
}