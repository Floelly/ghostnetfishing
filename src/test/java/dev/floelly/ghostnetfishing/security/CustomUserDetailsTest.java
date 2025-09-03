package dev.floelly.ghostnetfishing.security;

import dev.floelly.ghostnetfishing.model.Role;
import dev.floelly.ghostnetfishing.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


class CustomUserDetailsTest {

    public static final String TESTUSER = "testuser";
    public static final String PASSWORD = "secret";

    @Test
    void shouldBuildCustomUserDetailsCorrectly() {
        // given
        User user = new User();
        user.setUsername(TESTUSER);
        user.setPassword(PASSWORD);
        user.setEnabled(true);
        user.setRoles(Set.of(Role.STANDARD, Role.ADMIN));

        // when
        CustomUserDetails details = new CustomUserDetails(user);

        // then
        assertThat(details.getUsername()).isEqualTo(TESTUSER);
        assertThat(details.getPassword()).isEqualTo(PASSWORD);
        assertThat(details.isEnabled()).isTrue();
        assertThat(details.getAuthorities())
                .containsExactlyInAnyOrder(
                        new SimpleGrantedAuthority(Role.STANDARD.asSpringRole()),
                        new SimpleGrantedAuthority(Role.ADMIN.asSpringRole())
                );
    }
}