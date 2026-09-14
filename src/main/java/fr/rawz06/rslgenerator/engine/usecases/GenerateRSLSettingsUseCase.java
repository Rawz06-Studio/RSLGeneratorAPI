package fr.rawz06.rslgenerator.engine.usecases;

import fr.rawz06.rslgenerator.engine.domain.entities.Preset;
import fr.rawz06.rslgenerator.engine.domain.entities.SettingsFile;
import fr.rawz06.rslgenerator.engine.domain.ports.RSLScriptRunner;
import fr.rawz06.rslgenerator.engine.exceptions.ScriptErrorException;

import java.util.HashMap;
import java.util.Map;

public class GenerateRSLSettingsUseCase {

    private final RSLScriptRunner rslScriptRunner;

    public GenerateRSLSettingsUseCase(RSLScriptRunner rslScriptRunner) {
        this.rslScriptRunner = rslScriptRunner;
    }

    public SettingsFile generate(Preset preset) {
        SettingsFile generatedSettings = null;
        try {
            generatedSettings = rslScriptRunner.generateSettings(preset);
        } catch (RSLScriptRunner.ScriptExecutionException e) {
            throw new ScriptErrorException("Error generating settings", e);
        }

        Map<String, Object> pythonOutput = generatedSettings.settings();

        // Extract nested settings and put everything at top level
        @SuppressWarnings("unchecked")
        Map<String, Object> flatSettings = new HashMap<>((Map<String, Object>) pythonOutput.get("settings"));

        // Add hardcoded settings at top level
        flatSettings.put("show_seed_info", true);
        flatSettings.put("create_spoiler", true);
        flatSettings.put("password_lock", false);

        //Disable trade sequence to avoid broken seeds (only for ROT)
        if(preset.equals(Preset.ROT)) {
            flatSettings.put("adult_trade_start", new String[]{
                    "Prescription",
                    "Eyeball Frog",
                    "Eyedrops",
                    "Claim Check"
            });
        }

        return new SettingsFile(flatSettings);
    }
}
