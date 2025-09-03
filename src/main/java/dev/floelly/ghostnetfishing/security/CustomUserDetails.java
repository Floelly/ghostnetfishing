package dev.floelly.ghostnetfishing.security;

import dev.floelly.ghostnetfishing.model.Role;
import dev.floelly.ghostnetfishing.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class CustomUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final boolean enabled;
    private final Set<GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.enabled = user.isEnabled();
        this.authorities = user.getRoles().stream()
                .map(Role::asSpringRole)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
