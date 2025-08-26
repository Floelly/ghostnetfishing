package dev.floelly.ghostnetfishing.model;

import lombok.Getter;

@Getter
public class NetNotFoundException extends RuntimeException{
    private final Long netId;
    public NetNotFoundException(Long netId) {
        super(String.format("The requested net with id '%d' was not found.", netId));
        this.netId = netId;
    }
}
