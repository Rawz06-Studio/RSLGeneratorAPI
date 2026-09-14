package fr.rawz06.rslgenerator.web;

import fr.rawz06.rslgenerator.web.dto.PresetDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("version", appVersion);
        model.addAttribute("presets", PresetDto.values());
        return "index";
    }
}
