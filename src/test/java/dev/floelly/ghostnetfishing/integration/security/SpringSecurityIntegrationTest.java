package dev.floelly.ghostnetfishing.integration.security;

import dev.floelly.ghostnetfishing.model.Role;
import dev.floelly.ghostnetfishing.security.CustomUserDetails;
import dev.floelly.ghostnetfishing.testutil.AbstractMySQLContainerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(NoPasswordEncoderTestConfig.class)
public class SpringSecurityIntegrationTest extends AbstractMySQLContainerTest {

    private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";
    private static final String LOGIN_ENDPOINT = "/login";
    private static final String USERNAME_ADMIN = "admin";
    private static final String PASSWORD_ADMIN = "password";


    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAuthenticateAndPutUserIntoSecurityContext() throws Exception {
        MvcResult result = mockMvc.perform(post(LOGIN_ENDPOINT)
                        .param("username", USERNAME_ADMIN)
                        .param("password", PASSWORD_ADMIN)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        // then: SecurityContext prüfen
        SecurityContext context = (SecurityContext) result.getRequest()
                .getSession()
                .getAttribute(SPRING_SECURITY_CONTEXT);

        assertThat(context).isNotNull();
        assertThat(context.getAuthentication().getPrincipal())
                .isInstanceOf(CustomUserDetails.class);

        CustomUserDetails details = (CustomUserDetails) context.getAuthentication().getPrincipal();
        assertThat(details.getUsername()).isEqualTo(USERNAME_ADMIN);
        assertThat(details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .containsExactlyInAnyOrder(Role.STANDARD.asSpringRole(), Role.ADMIN.asSpringRole(), Role.RECOVERER.asSpringRole());
    }
}
