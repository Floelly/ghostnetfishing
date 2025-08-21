package dev.floelly.ghostnetfishing.model;

public enum NetSize {
    S("Diameter up to 10 m"),
    M("Diameter up to 30 m"),
    L("Diameter up to 100 m"),
    XL("Diameter above 100 m");


    private final String description;

    NetSize(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
