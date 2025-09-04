package dev.floelly.ghostnetfishing.integration.security;

import dev.floelly.ghostnetfishing.testutil.AbstractH2Test;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static dev.floelly.ghostnetfishing.testutil.FrontEndTestFunctions.assertToastMessageExists;
import static dev.floelly.ghostnetfishing.testutil.MvcTestFunctions.getSession;
import static dev.floelly.ghostnetfishing.testutil.MvcTestFunctions.sendGetRequestToNetsPage;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RequestNetRecoverySecurityTest extends AbstractH2Test {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRedirectToLogin_whenAnonymousUser_onRequestNetRecovery() throws Exception {
        mockMvc.perform(post(String.format(REQUEST_NET_RECOVERY_ENDPOINT, getRandomNetId()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**"+LOGIN_ENDPOINT));
    }

    @Test
    @WithMockUser(roles = {SPRING_SECURITY_RECOVERER_ROLE})
    void shouldRedirect_whenUserHasRights_onRequestNetRecovery() throws Exception {
        mockMvc.perform(post(String.format(REQUEST_NET_RECOVERY_ENDPOINT, Long.valueOf(REPORTED_NET_ID)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(NETS_ENDPOINT));
    }

    @Test
    @WithMockUser(roles = {SPRING_SECURITY_STANDARD_ROLE})
    void shouldDenyAccess_whenUserLacksRights_onRequestNetRecovery() throws Exception {
        MvcResult result = mockMvc.perform(post(String.format(REQUEST_NET_RECOVERY_ENDPOINT, getRandomNetId()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(NETS_ENDPOINT))
                .andReturn();
        MockHttpSession session = getSession(result);
        Document doc = sendGetRequestToNetsPage(mockMvc, session);
        assertToastMessageExists(doc, NO_PERMISSION_TOAST_MESSAGE);
    }
}
