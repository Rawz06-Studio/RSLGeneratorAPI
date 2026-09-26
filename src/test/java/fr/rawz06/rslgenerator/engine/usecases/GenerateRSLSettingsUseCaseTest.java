package fr.rawz06.rslgenerator.engine.usecases;

import fr.rawz06.rslgenerator.engine.domain.entities.Preset;
import fr.rawz06.rslgenerator.engine.domain.entities.SettingsFile;
import fr.rawz06.rslgenerator.engine.domain.ports.input.ApplySettingsOverrides;
import fr.rawz06.rslgenerator.engine.domain.ports.output.RSLScriptRunner;
import fr.rawz06.rslgenerator.engine.exceptions.ScriptErrorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenerateRSLSettingsUseCaseTest {

    private FakeRSLScriptRunner scriptRunner;
    private FakeApplySettingsOverrides applyOverrides;
    private GenerateRSLSettingsUseCase useCase;

    @BeforeEach
    void setUp() {
        scriptRunner = new FakeRSLScriptRunner();
        applyOverrides = new FakeApplySettingsOverrides();
        useCase = new GenerateRSLSettingsUseCase(scriptRunner, applyOverrides);
    }

    @Test
    @DisplayName("Forwards preset to script runner")
    void forwardsPresetToScriptRunner() {
        scriptRunner.willReturn(new SettingsFile(Map.of("open_forest", "closed")));

        useCase.generate(Preset.RSL);

        assertThat(scriptRunner.receivedPreset()).isEqualTo(Preset.RSL);
    }

    @Test
    @DisplayName("Delegates runner result and preset to ApplySettingsOverrides")
    void delegatesToApplyOverrides() {
        SettingsFile baseSettings = new SettingsFile(Map.of("open_forest", "closed"));
        scriptRunner.willReturn(baseSettings);

        useCase.generate(Preset.ROT);

        assertThat(applyOverrides.lastSettingsFile()).isSameAs(baseSettings);
        assertThat(applyOverrides.lastPreset()).isEqualTo(Preset.ROT);
    }

    @Test
    @DisplayName("Returns the result produced by ApplySettingsOverrides")
    void returnsOverridesResult() {
        SettingsFile base = new SettingsFile(Map.of());
        SettingsFile overrideResult = new SettingsFile(Map.of("show_seed_info", true));
        scriptRunner.willReturn(base);
        applyOverrides.willReturn(overrideResult);

        SettingsFile result = useCase.generate(Preset.RSL);

        assertThat(result).isSameAs(overrideResult);
    }

    @Test
    @DisplayName("Wraps ScriptExecutionException in ScriptErrorException")
    void wrapsScriptException() {
        scriptRunner.willFailWith(
                new RSLScriptRunner.ScriptExecutionException("Script failed with exit code 1"));

        assertThatThrownBy(() -> useCase.generate(Preset.RSL))
                .isInstanceOf(ScriptErrorException.class)
                .hasMessage("Error generating settings")
                .cause()
                .hasMessage("Script failed with exit code 1");
    }

    private static class FakeRSLScriptRunner implements RSLScriptRunner {

        private SettingsFile result;
        private ScriptExecutionException failure;
        private Preset capturedPreset;

        void willReturn(SettingsFile result) {
            this.result = result;
        }

        void willFailWith(ScriptExecutionException failure) {
            this.failure = failure;
        }

        Preset receivedPreset() {
            return capturedPreset;
        }

        @Override
        public SettingsFile generateSettings(Preset preset) throws ScriptExecutionException {
            this.capturedPreset = preset;
            if (failure != null) {
                throw failure;
            }
            return result;
        }
    }

    private static class FakeApplySettingsOverrides implements ApplySettingsOverrides {

        private SettingsFile lastSettingsFile;
        private Preset lastPreset;
        private SettingsFile toReturn;

        void willReturn(SettingsFile toReturn) {
            this.toReturn = toReturn;
        }

        SettingsFile lastSettingsFile() {
            return lastSettingsFile;
        }

        Preset lastPreset() {
            return lastPreset;
        }

        @Override
        public SettingsFile apply(SettingsFile settingsFile, Preset preset) {
            this.lastSettingsFile = settingsFile;
            this.lastPreset = preset;
            return toReturn != null ? toReturn : settingsFile;
        }
    }
}