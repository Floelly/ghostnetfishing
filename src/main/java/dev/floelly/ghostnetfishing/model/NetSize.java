package dev.floelly.ghostnetfishing.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NetSize {
    S("Diameter up to 10 m"),
    M("Diameter up to 30 m"),
    L("Diameter up to 100 m"),
    XL("Diameter over 100 m");


    private final String description;

}
