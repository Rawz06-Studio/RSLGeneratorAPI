package fr.rawz06.rslgenerator.engine.domain.ports.input;

import fr.rawz06.rslgenerator.engine.domain.entities.Preset;
import fr.rawz06.rslgenerator.engine.domain.entities.SettingsFile;

public interface ApplySettingsOverrides {
    SettingsFile apply(SettingsFile settingsFile, Preset preset);
}
