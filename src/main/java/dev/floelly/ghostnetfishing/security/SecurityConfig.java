package dev.floelly.ghostnetfishing.security;

import dev.floelly.ghostnetfishing.dto.ToastMessageResponse;
import dev.floelly.ghostnetfishing.dto.ToastType;
import dev.floelly.ghostnetfishing.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/login", "/logout").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/", "/nets", "/nets/new").permitAll()
                        .requestMatchers(HttpMethod.POST, "/nets/new").permitAll()
                        .requestMatchers(HttpMethod.POST, "/nets/{id}/*").hasRole(Role.STANDARD.name())
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/nets")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            FlashMap flashMap = new FlashMap();
                            flashMap.put("toastMessages", List.of(new ToastMessageResponse("You have been logged out.", ToastType.INFO)));
                            new SessionFlashMapManager().saveOutputFlashMap(flashMap, request, response);
                            response.sendRedirect("/nets");
                        })
                        .permitAll()
                )
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            FlashMap flashMap = new FlashMap();
                            flashMap.put("toastMessages", List.of(
                                    new ToastMessageResponse("You do not have permission to perform this action.", ToastType.WARNING)
                            ));
                            new SessionFlashMapManager().saveOutputFlashMap(flashMap, request, response);

                            response.sendRedirect("/nets");
                        })
                );

        return http.build();
    }

    @Bean
    @Profile("!mysql-container-test & !h2-test")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
