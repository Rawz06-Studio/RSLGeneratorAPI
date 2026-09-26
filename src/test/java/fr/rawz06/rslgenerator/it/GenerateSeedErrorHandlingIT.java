package fr.rawz06.rslgenerator.it;

import fr.rawz06.rslgenerator.api.python.PythonRSLScriptAdapter;
import fr.rawz06.rslgenerator.engine.domain.entities.Preset;
import fr.rawz06.rslgenerator.engine.domain.ports.output.RSLScriptRunner;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DisplayName("Error scenarios for Python RSL Script Adapter")
class GenerateSeedErrorHandlingIT {

    private static Path scriptDirPath;

    @TempDir
    static Path tempDir;

    private static String failingScriptName = "generate_settings.sh";
    private static String missingPatternScriptName = "generate_settings.sh";
    private static String malformedJsonScriptName = "generate_settings.sh";

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("app.python.command", () -> "bash");
        registry.add("app.python.script.dir", () -> scriptDirPath.toAbsolutePath().toString());
        registry.add("app.python.script.name", () -> failingScriptName);
        registry.add("app.python.weights.rsl", () -> "weights/rsl_season7.json");
        registry.add("app.python.weights.rot", () -> "weights/rsl_rot.json");
    }

    @BeforeAll
    static void setUpBeforeAll() throws IOException {
        scriptDirPath = tempDir.resolve("fake_scripts");
        Files.createDirectories(scriptDirPath.resolve("data"));
    }

    @Test
    @DisplayName("Should throw ScriptExecutionException when script exits with non-zero code")
    void shouldThrowExceptionWhenScriptFails() throws IOException {
        // Create a failing script
        Path failingScript = scriptDirPath.resolve("generate_settings.sh");
        String scriptContent = """
                #!/bin/bash
                echo "Script execution failed: Invalid parameters"
                exit 1
                """;
        Files.writeString(failingScript, scriptContent);
        failingScript.toFile().setExecutable(true);
        
        PythonRSLScriptAdapter adapter = new PythonRSLScriptAdapter(new ObjectMapper());
        setAdapterFields(adapter, scriptDirPath.toAbsolutePath().toString(), "generate_settings.sh");
        
        assertThatThrownBy(() -> adapter.generateSettings(Preset.RSL))
                .isInstanceOf(RSLScriptRunner.ScriptExecutionException.class)
                .hasMessageContaining("exit code")
                .hasMessageContaining("1");
    }

    @Test
    @DisplayName("Should throw ScriptExecutionException when script output missing 'Plando File' pattern")
    void shouldThrowExceptionWhenFilenamePatternMissing() throws IOException {
        // Create a script that doesn't output the expected pattern
        Path noPatternScript = scriptDirPath.resolve("generate_settings.sh");
        String scriptContent = """
                #!/bin/bash
                mkdir -p data
                cat > "data/plando_1.json" << 'EOF'
                {
                  "settings": {
                    "open_forest": "closed"
                  }
                }
                EOF
                echo "Script completed successfully"
                """;
        Files.writeString(noPatternScript, scriptContent);
        noPatternScript.toFile().setExecutable(true);
        
        PythonRSLScriptAdapter adapter = new PythonRSLScriptAdapter(new ObjectMapper());
        setAdapterFields(adapter, scriptDirPath.toAbsolutePath().toString(), "generate_settings.sh");
        
        assertThatThrownBy(() -> adapter.generateSettings(Preset.RSL))
                .isInstanceOf(RSLScriptRunner.ScriptExecutionException.class)
                .hasMessageContaining("Plando File");
    }

    @Test
    @DisplayName("Should throw ScriptExecutionException when JSON file is malformed")
    void shouldThrowExceptionWhenJsonIsMalformed() throws IOException {
        // Create a script that outputs malformed JSON
        Path badJsonScript = scriptDirPath.resolve("generate_settings.sh");
        String scriptContent = """
                #!/bin/bash
                mkdir -p data
                cat > "data/plando_1.json" << 'EOF'
                {
                  "settings": {
                    "open_forest": "closed"
                  THIS IS INVALID JSON
                }
                EOF
                echo "Plando File: plando_1.json"
                """;
        Files.writeString(badJsonScript, scriptContent);
        badJsonScript.toFile().setExecutable(true);
        
        PythonRSLScriptAdapter adapter = new PythonRSLScriptAdapter(new ObjectMapper());
        setAdapterFields(adapter, scriptDirPath.toAbsolutePath().toString(), "generate_settings.sh");
        
        assertThatThrownBy(() -> adapter.generateSettings(Preset.RSL))
                .isInstanceOf(RSLScriptRunner.ScriptExecutionException.class);
    }

    private void setAdapterFields(PythonRSLScriptAdapter adapter, String scriptDir, String scriptName) {
        try {
            var pythonCommandField = PythonRSLScriptAdapter.class.getDeclaredField("pythonCommand");
            pythonCommandField.setAccessible(true);
            pythonCommandField.set(adapter, "bash");
            
            var scriptDirField = PythonRSLScriptAdapter.class.getDeclaredField("scriptDir");
            scriptDirField.setAccessible(true);
            scriptDirField.set(adapter, scriptDir);
            
            var scriptNameField = PythonRSLScriptAdapter.class.getDeclaredField("scriptName");
            scriptNameField.setAccessible(true);
            scriptNameField.set(adapter, scriptName);
            
            var rslWeightField = PythonRSLScriptAdapter.class.getDeclaredField("rslWeight");
            rslWeightField.setAccessible(true);
            rslWeightField.set(adapter, "weights/rsl.json");
            
            var rotWeightField = PythonRSLScriptAdapter.class.getDeclaredField("rotWeight");
            rotWeightField.setAccessible(true);
            rotWeightField.set(adapter, "weights/rot.json");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set adapter fields", e);
        }
    }
}
