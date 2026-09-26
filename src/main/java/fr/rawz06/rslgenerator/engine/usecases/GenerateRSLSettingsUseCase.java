package fr.rawz06.rslgenerator.engine.usecases;

import fr.rawz06.rslgenerator.engine.domain.entities.Preset;
import fr.rawz06.rslgenerator.engine.domain.entities.SettingsFile;
import fr.rawz06.rslgenerator.engine.domain.ports.input.ApplySettingsOverrides;
import fr.rawz06.rslgenerator.engine.domain.ports.input.GenerateRSLSettings;
import fr.rawz06.rslgenerator.engine.domain.ports.output.RSLScriptRunner;
import fr.rawz06.rslgenerator.engine.exceptions.ScriptErrorException;

import java.util.HashMap;
import java.util.Map;

public class GenerateRSLSettingsUseCase implements GenerateRSLSettings {

    private final RSLScriptRunner rslScriptRunner;
    private final ApplySettingsOverrides applySettingsOverrides;

    public GenerateRSLSettingsUseCase(RSLScriptRunner rslScriptRunner, ApplySettingsOverrides applySettingsOverrides) {
        this.rslScriptRunner = rslScriptRunner;
        this.applySettingsOverrides = applySettingsOverrides;
    }

    public SettingsFile generate(Preset preset) {
        SettingsFile generatedSettings = getGeneratedSettings(preset);
        return applySettingsOverrides.apply(generatedSettings, preset);
    }

    private SettingsFile getGeneratedSettings(Preset preset) {
        try {
            return rslScriptRunner.generateSettings(preset);
        } catch (RSLScriptRunner.ScriptExecutionException e) {
            throw new ScriptErrorException("Error generating settings", e);
        }
    }
}
