package fr.rawz06.rslgenerator.web.mapper;

import fr.rawz06.rslgenerator.engine.domain.entities.SettingsFile;
import fr.rawz06.rslgenerator.web.dto.SettingsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SettingsMapper {

    @Mapping(target = "settings", source = "settings")
    SettingsDto toDto(SettingsFile settings);
}
