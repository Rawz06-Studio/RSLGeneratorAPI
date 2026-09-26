package fr.rawz06.rslgenerator.web;

import fr.rawz06.rslgenerator.engine.domain.ports.input.GenerateRSLSettings;
import fr.rawz06.rslgenerator.web.dto.PresetDto;
import fr.rawz06.rslgenerator.web.dto.SettingsDto;
import fr.rawz06.rslgenerator.web.mapper.PresetMapper;
import fr.rawz06.rslgenerator.web.mapper.SettingsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rsl")
@RequiredArgsConstructor
public class RSLSettingGeneratorController {

    private final GenerateRSLSettings generateRSLSettings;
    private final PresetMapper presetMapper;
    private final SettingsMapper settingsMapper;

    @GetMapping("/{preset}")
    public SettingsDto generateSettings(@PathVariable PresetDto preset) {
        return settingsMapper.toDto(generateRSLSettings.generate(presetMapper.toEntity(preset)));
    }
}
