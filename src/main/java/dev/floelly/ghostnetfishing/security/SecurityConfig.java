package dev.floelly.ghostnetfishing.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/login", "/logout").permitAll()
                        .requestMatchers(HttpMethod.GET, "/", "/nets", "/nets/new").permitAll()
                        .requestMatchers(HttpMethod.POST, "/nets/new").permitAll() //TODO: add "/nets/{id}/call-lost" later
                        .requestMatchers(HttpMethod.POST, "/nets/{id}/request-recovery").hasRole("STANDARD")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.loginPage("/login").permitAll());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("regular-user")
                .password("password")
                .roles("STANDARD")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
