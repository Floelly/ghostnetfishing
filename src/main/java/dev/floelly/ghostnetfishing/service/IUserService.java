package dev.floelly.ghostnetfishing.service;

import dev.floelly.ghostnetfishing.model.User;

public interface IUserService {
    User findByUsername(String username);
}
