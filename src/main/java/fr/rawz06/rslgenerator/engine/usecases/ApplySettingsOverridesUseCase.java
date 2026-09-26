package fr.rawz06.rslgenerator.engine.usecases;

import fr.rawz06.rslgenerator.engine.domain.entities.Preset;
import fr.rawz06.rslgenerator.engine.domain.entities.SettingsFile;
import fr.rawz06.rslgenerator.engine.domain.ports.input.ApplySettingsOverrides;

import java.util.HashMap;
import java.util.Map;

public class ApplySettingsOverridesUseCase implements ApplySettingsOverrides {

    private static final String[] ADULT_TRADE_START = {
            "Prescription", "Eyeball Frog", "Eyedrops", "Claim Check"
    };

    @Override
    public SettingsFile apply(SettingsFile settingsFile, Preset preset) {
        Map<String, Object> overridden = new HashMap<>(settingsFile.settings());

        overridden.put("show_seed_info", true);
        overridden.put("create_spoiler", true);
        overridden.put("password_lock", false);

        if (preset == Preset.ROT) {
            overridden.put("adult_trade_start", ADULT_TRADE_START);
        }

        return new SettingsFile(overridden);
    }
}
