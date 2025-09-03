package dev.floelly.ghostnetfishing.security;

import dev.floelly.ghostnetfishing.model.Role;
import dev.floelly.ghostnetfishing.model.User;
import dev.floelly.ghostnetfishing.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows; // ✅ JUnit5!

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    public static final String TESTUSER = "testuser";
    public static final String PASSWORD = "secret";
    private static final String UNKNOWN_USER = "unknown";
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldLoadUserByUsernameSuccessfully() {
        User user = new User();
        user.setUsername(TESTUSER);
        user.setPassword(PASSWORD);
        user.setEnabled(true);
        user.setRoles(Set.of(Role.STANDARD));

        when(userRepository.findByUsername(TESTUSER)).thenReturn(Optional.of(user));

        UserDetails details = customUserDetailsService.loadUserByUsername(TESTUSER);

        assertThat(details).isInstanceOf(CustomUserDetails.class);
        assertThat(details.getUsername()).isEqualTo(TESTUSER);
        assertThat(details.getPassword()).isEqualTo(PASSWORD);
        assertThat(details.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet()))
                .containsExactlyInAnyOrder(Role.STANDARD.asSpringRole());

        verify(userRepository).findByUsername(TESTUSER);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername(UNKNOWN_USER)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(UNKNOWN_USER));

        verify(userRepository).findByUsername(UNKNOWN_USER);
    }
}
