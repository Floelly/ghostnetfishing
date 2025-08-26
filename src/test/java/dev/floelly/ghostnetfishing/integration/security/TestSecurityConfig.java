package dev.floelly.ghostnetfishing.integration.security;

import dev.floelly.ghostnetfishing.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class TestSecurityConfig {
    @Bean
    @Profile("h2-test")
    public UserDetailsService userDetailsService() {
        @SuppressWarnings("deprecation")
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles(Role.STANDARD.name())
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
