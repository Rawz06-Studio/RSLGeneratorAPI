package fr.rawz06.rslgenerator.web.mapper;

import fr.rawz06.rslgenerator.engine.domain.entities.Preset;
import fr.rawz06.rslgenerator.web.dto.PresetDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PresetMapper {

    Preset toEntity(PresetDto presetDto);
}
