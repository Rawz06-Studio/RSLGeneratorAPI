package fr.rawz06.rslgenerator.web.dto;

import java.util.Map;

public record SettingsDto(
        Map<String, Object> settings
) {
    public SettingsDto {
        if (settings == null) {
            throw new IllegalArgumentException("Settings cannot be null");
        }
    }
}
