package fr.rawz06.rslgenerator.engine.usecases;

import fr.rawz06.rslgenerator.engine.domain.entities.Preset;
import fr.rawz06.rslgenerator.engine.domain.entities.SettingsFile;

import java.util.HashMap;
import java.util.Map;

public class GenerateRSLSettingsProxyUseCase {
    private final GenerateRSLSettingsUseCase generateRSLSettingsUseCase;
    private final Map<Preset, SettingsFile> cache = new HashMap<>();

    public GenerateRSLSettingsProxyUseCase(GenerateRSLSettingsUseCase generateRSLSettingsUseCase) {
        this.generateRSLSettingsUseCase = generateRSLSettingsUseCase;
        for (Preset value : Preset.values()) {
            cache.put(value, generateRSLSettingsUseCase.generate(value));
        }
    }

    public SettingsFile generate(Preset preset) {
        SettingsFile settingsFile = cache.get(preset);
        if(settingsFile != null) {
            settingsFile = generateRSLSettingsUseCase.generate(preset);
        }
        cache.put(preset, generateRSLSettingsUseCase.generate(preset));
        return settingsFile;
    }
}
