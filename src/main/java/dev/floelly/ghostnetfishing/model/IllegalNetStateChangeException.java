package dev.floelly.ghostnetfishing.model;

import lombok.Getter;

@Getter
public class IllegalNetStateChangeException extends RuntimeException {
    private final Long netId;
    private final NetState currentState;
    private final NetState newState;
    public IllegalNetStateChangeException(Long netId, NetState currentState, NetState newState) {
        super(String.format("The net with id '%d' cannot switch state from '%s' to '%s'. This state change is not valid.", netId, currentState.name(), newState.name()));
        this.netId = netId;
        this.currentState = currentState;
        this.newState = newState;
    }
}
