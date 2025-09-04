package dev.floelly.ghostnetfishing.service;

import dev.floelly.ghostnetfishing.model.User;
import dev.floelly.ghostnetfishing.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.createDefaultUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnUser_whenUsernameExists() {
        // given
        User user = createDefaultUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        // when
        User result = userService.findByUsername(user.getUsername());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    void shouldThrowUsernameNotFoundException_whenUsernameDoesNotExist() {
        // Arrange
        String username = "doesNotExist";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.findByUsername(username));
    }
}