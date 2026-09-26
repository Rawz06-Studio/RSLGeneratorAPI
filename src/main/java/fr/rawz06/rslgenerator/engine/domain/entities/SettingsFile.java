package fr.rawz06.rslgenerator.engine.domain.entities;

import java.util.Map;

/**
 * Represents a complete settings file for the randomizer.
 * Contains all the configuration needed to generate a seed.
 */
public record SettingsFile(
        Map<String, Object> settings
) {
    public SettingsFile {
        if (settings == null) {
            throw new IllegalArgumentException("Settings cannot be null");
        }
        settings = Map.copyOf(settings); // Make the map unmodifiable
    }
}

