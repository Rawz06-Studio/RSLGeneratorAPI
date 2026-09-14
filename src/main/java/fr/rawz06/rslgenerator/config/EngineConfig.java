package fr.rawz06.rslgenerator.config;

import fr.rawz06.rslgenerator.engine.domain.ports.RSLScriptRunner;
import fr.rawz06.rslgenerator.engine.usecases.GenerateRSLSettingsUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EngineConfig {

    @Bean
    public GenerateRSLSettingsUseCase generateRSLSettingsUseCase(RSLScriptRunner rslScriptRunner) {
        return new GenerateRSLSettingsUseCase(rslScriptRunner);
    }
}
