package fr.rawz06.rslgenerator.engine.domain.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum Preset {
    RSL("rsl"),
    ROT("rot");

    private final String name;
}

