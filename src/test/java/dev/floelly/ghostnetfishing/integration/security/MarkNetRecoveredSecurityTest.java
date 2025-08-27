package dev.floelly.ghostnetfishing.integration.security;

import dev.floelly.ghostnetfishing.testutil.AbstractH2Test;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class MarkNetRecoveredSecurityTest extends AbstractH2Test {
    private static final Long NET_ID = 1L;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username ="no-right-user", roles = {})
    void shouldDenyAccess_whenUserLacksRights_onMarkNetRecovered() throws Exception {
        mockMvc.perform(post(String.format(MARK_NET_RECOVERED_ENDPOINT, NET_ID))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRedirectToLogin_whenAnonymousUser_onMarkNetRecovered() throws Exception {
        mockMvc.perform(post(String.format(MARK_NET_RECOVERED_ENDPOINT, NET_ID))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**"+LOGIN_ENDPOINT));
    }

    @Test
    @WithMockUser(username ="regular-user", roles = {STANDARD_ROLE})
    void shouldRedirect_whenUserHasRights_onMarkNetRecovered() throws Exception {
        mockMvc.perform(post(String.format(MARK_NET_RECOVERED_ENDPOINT, NET_ID))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(NETS_ENDPOINT));
    }
}
