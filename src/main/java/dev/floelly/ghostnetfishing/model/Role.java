package dev.floelly.ghostnetfishing.model;

public enum Role {
    ADMIN,
    STANDARD,
    RECOVERER;

    public String asSpringRole() {
        return "ROLE_" + this.name();
    }
}
