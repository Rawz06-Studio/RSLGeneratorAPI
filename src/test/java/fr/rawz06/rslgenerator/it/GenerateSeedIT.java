package fr.rawz06.rslgenerator.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DisplayName("End-to-end test for RSL Settings generation")
class GenerateSeedIT {

    private static Path scriptDirPath;

    @TempDir
    static Path tempDir;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("app.python.command", () -> "bash");
        registry.add("app.python.script.dir", () -> scriptDirPath.toAbsolutePath().toString());
        registry.add("app.python.script.name", () -> "generate_settings.sh");
        registry.add("app.python.weights.rsl", () -> "weights/rsl_season7.json");
        registry.add("app.python.weights.rot", () -> "weights/rsl_rot.json");
    }

    @BeforeAll
    static void setUpBeforeAll() throws IOException {
        // Create directory structure
        scriptDirPath = tempDir.resolve("fake_scripts");
        Path dataDir = scriptDirPath.resolve("data");
        Files.createDirectories(dataDir);

        // Create fake shell script
        createFakeScriptStatic();
    }

    @BeforeEach
    void setUp() throws IOException {
        // Initialize MockMvc with the application context
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        
        // Ensure data directory exists
        Path dataDir = scriptDirPath.resolve("data");
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
        
        // Clean up any leftover files from previous tests (including backups)
        Files.list(scriptDirPath)
                .filter(file -> file.getFileName().toString().contains(".bak") || 
                               file.getFileName().toString().startsWith("generate_settings"))
                .forEach(file -> {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        // Ignore
                    }
                });
        
        // Recreate the main script
        try {
            createFakeScriptStatic();
        } catch (IOException e) {
            // Script might already exist, that's ok
        }
        
        // Clean data directory
        Files.list(dataDir)
                .forEach(file -> {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        // Ignore
                    }
                });
    }

    @Test
    @DisplayName("Should generate RSL settings end-to-end without mocking business logic")
    void generateRSLSettingsEndToEnd() throws Exception {
        mockMvc.perform(get("/api/rsl/RSL"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.settings", notNullValue()))
                .andExpect(jsonPath("$.settings.open_forest").exists())
                .andExpect(jsonPath("$.settings.some_setting", notNullValue()));
    }

    @Test
    @DisplayName("Should generate ROT settings end-to-end without mocking business logic")
    void generateROTSettingsEndToEnd() throws Exception {
        mockMvc.perform(get("/api/rsl/ROT"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.settings", notNullValue()))
                .andExpect(jsonPath("$.settings.open_forest").exists())
                .andExpect(jsonPath("$.settings.some_setting", notNullValue()));
    }

    @Test
    @DisplayName("Should reject invalid preset with 400 Bad Request")
    void shouldRejectInvalidPreset() throws Exception {
        mockMvc.perform(get("/api/rsl/INVALID"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    /**
     * Creates a fake shell script that simulates the Python RandomSettingsGenerator.py script.
     * The script generates a valid JSON file with RSL settings and outputs the expected format.
     */
    private static void createFakeScriptStatic() throws IOException {
        // Create a shell script that generates a fake settings file
        Path scriptPath = scriptDirPath.resolve("generate_settings.sh");

        String scriptContent = """
                #!/bin/bash
                
                # Fake RandomSettingsGenerator.py script
                # Accept all arguments but always generate the same output file
                # (ignores --override, --no_seed, etc.)
                
                # Create output directory if it doesn't exist
                mkdir -p data
                
                # Always use the same filename for simplicity
                OUTPUT_FILE="plando_1.json"
                
                # Generate fake settings JSON
                cat > "data/$OUTPUT_FILE" << 'EOFDATA'
                {
                  "settings": {
                    "open_forest": "closed",
                    "open_doors": "blue",
                    "some_setting": "some_value",
                    "enemy_shuffle": "chaos",
                    "boss_shuffle": "chaos",
                    "enemy_damage": "default",
                    "enemy_health": "default"
                  }
                }
                EOFDATA
                
                # Output the expected format
                echo "Plando File: $OUTPUT_FILE"
                """;

        Files.writeString(scriptPath, scriptContent);
        // Make script executable
        scriptPath.toFile().setExecutable(true);
    }

    /**
     * Creates a script that fails (exits with non-zero code).
     */
    private static void createFailingScriptStatic() throws IOException {
        Path scriptPath = scriptDirPath.resolve("generate_settings_fail.sh");

        String scriptContent = """
                #!/bin/bash
                # Fake script that fails
                echo "Script execution failed: Invalid parameters"
                exit 1
                """;

        Files.writeString(scriptPath, scriptContent);
        scriptPath.toFile().setExecutable(true);
    }
}
