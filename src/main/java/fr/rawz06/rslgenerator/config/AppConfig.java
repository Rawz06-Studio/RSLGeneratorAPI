package fr.rawz06.rslgenerator.config;

import fr.rawz06.rslgenerator.engine.domain.ports.input.ApplySettingsOverrides;
import fr.rawz06.rslgenerator.engine.domain.ports.input.GenerateRSLSettings;
import fr.rawz06.rslgenerator.engine.domain.ports.output.RSLScriptRunner;
import fr.rawz06.rslgenerator.engine.usecases.ApplySettingsOverridesUseCase;
import fr.rawz06.rslgenerator.engine.usecases.GenerateRSLSettingsUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public GenerateRSLSettings generateRSLSettingsUseCase(RSLScriptRunner rslScriptRunner, ApplySettingsOverrides applySettingsOverrides) {
        return new GenerateRSLSettingsUseCase(rslScriptRunner, applySettingsOverrides);
    }

    @Bean
    public ApplySettingsOverrides applySettingsOverrides() {
        return new ApplySettingsOverridesUseCase();
    }
}
