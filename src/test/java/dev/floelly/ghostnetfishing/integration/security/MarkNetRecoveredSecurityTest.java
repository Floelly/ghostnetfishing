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
import static dev.floelly.ghostnetfishing.testutil.MvcTestFunctions.*;
import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class MarkNetRecoveredSecurityTest extends AbstractH2Test {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRedirectToLogin_whenAnonymousUser_onMarkNetRecovered() throws Exception {
        mockMvc.perform(post(String.format(MARK_NET_RECOVERED_ENDPOINT, getRandomNetId()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**"+LOGIN_ENDPOINT));
    }

    @Test
    @WithMockUser(roles = {SPRING_SECURITY_STANDARD_ROLE})
    void shouldRedirect_whenUserHasRights_onMarkNetRecovered() throws Exception {
        sendPostRequestAndExpectRedirectToNetsPage(mockMvc, String.format(MARK_NET_RECOVERED_ENDPOINT, Long.valueOf(RECOVERY_PENDING_NET_ID)));
    }

    @Test
    @WithMockUser(roles = {})
    void shouldDenyAccess_whenLacksRights_onMarkNetRecovered() throws Exception {
        MvcResult result = sendPostRequestAndExpectRedirectToNetsPage(mockMvc, String.format(MARK_NET_RECOVERED_ENDPOINT, getRandomNetId()));
        MockHttpSession session = getSession(result);
        Document doc = sendGetRequestToNetsPage(mockMvc, session);
        assertToastMessageExists(doc, "not have permission");
    }

    @Test
    @WithMockUser(username="userwithnumber", roles = {SPRING_SECURITY_STANDARD_ROLE})
    void shouldDenyAccess_whenUserDoesNotOwnNet_onMarkNetRecovered() throws Exception {
        MvcResult result = sendPostRequestAndExpectRedirectToNetsPage(mockMvc, String.format(MARK_NET_RECOVERED_ENDPOINT, Long.valueOf(RECOVERY_PENDING_NET_ID)));
        MockHttpSession session = getSession(result);
        Document doc = sendGetRequestToNetsPage(mockMvc, session);
        assertToastMessageExists(doc, "not have permission");
    }
}
