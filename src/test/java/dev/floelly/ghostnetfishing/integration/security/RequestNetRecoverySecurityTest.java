package dev.floelly.ghostnetfishing.integration.security;

import dev.floelly.ghostnetfishing.testutil.AbstractH2Test;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;

public class RequestNetRecoverySecurityTest extends AbstractH2Test {
    private static final Long NET_ID = 0L;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username ="no-right-user", roles = {})
    void shouldDenyAccess_whenUserLacksRights_onRequestNetRecovery() throws Exception {
        mockMvc.perform(post(String.format(REQUEST_NET_RECOVERY_ENDPOINT, NET_ID)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRedirectToLogin_whenAnonymousUser_onRequestNetRecovery() throws Exception {
        mockMvc.perform(post(String.format(REQUEST_NET_RECOVERY_ENDPOINT, NET_ID)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_ENDPOINT));
    }

    @Test
    @WithMockUser(username ="regular-user", roles = {STANDARD_ROLE})
    void shouldRedirect_whenUserHasRights_onRequestNetRecovery() throws Exception {
        mockMvc.perform(post(String.format(REQUEST_NET_RECOVERY_ENDPOINT, NET_ID)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(NETS_ENDPOINT));
    }
}
