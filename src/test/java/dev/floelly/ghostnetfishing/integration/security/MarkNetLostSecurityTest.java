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

public class MarkNetLostSecurityTest extends AbstractH2Test {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRedirectToLogin_whenAnonymousUser_onMarkNetLost() throws Exception {
        mockMvc.perform(post(String.format(MARK_NET_LOST_ENDPOINT, getRandomNetId()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**"+LOGIN_ENDPOINT));
    }

    @Test
    @WithMockUser(roles = {SPRING_SECURITY_STANDARD_ROLE})
    void shouldRedirect_whenUserHasRights_onMarkNetLost() throws Exception {
        sendPostRequestAndExpectRedirectToNetsPage(mockMvc, String.format(MARK_NET_LOST_ENDPOINT, Long.valueOf(RECOVERY_PENDING_NET_ID)));
    }

    @Test
    @WithMockUser(roles = {})
    void shouldDenyAccess_whenLacksRights_onMarkNetLost() throws Exception {
        MvcResult result = sendPostRequestAndExpectRedirectToNetsPage(mockMvc, String.format(MARK_NET_LOST_ENDPOINT, getRandomNetId()));
        MockHttpSession session = getSession(result);
        Document doc = sendGetRequestToNetsPage(mockMvc, session);
        assertToastMessageExists(doc, NO_PERMISSION_TOAST_MESSAGE);
    }
}
