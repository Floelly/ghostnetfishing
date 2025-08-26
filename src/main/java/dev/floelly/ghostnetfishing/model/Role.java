package dev.floelly.ghostnetfishing.model;

public enum Role {
    ADMIN,
    STANDARD;

    public String asSpringRole() {
        return "ROLE_" + this.name();
    }
}
