package fr.rawz06.rslgenerator.web;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MainController {

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @GetMapping("/")
    public String index() {
        return "Hello World from " + appVersion;
    }
}
