package fr.rawz06.rslgenerator.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MainController {

    public String index() {
        return "Hello World!";
    }
}
