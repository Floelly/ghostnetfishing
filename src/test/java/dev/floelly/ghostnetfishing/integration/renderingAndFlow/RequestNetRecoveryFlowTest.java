package dev.floelly.ghostnetfishing.integration.renderingAndFlow;

import dev.floelly.ghostnetfishing.testutil.AbstractH2Test;
import dev.floelly.ghostnetfishing.testutil.TestDataFactory;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;

@Sql(scripts = "/sql/populate-nets-table-diverse.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class RequestNetRecoveryFlowTest extends AbstractH2Test {
    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest(name = "net {0} should have status {1} after request net recovery")
    @CsvSource({
            TestDataFactory.REPORTED_NET_ID + "," + RECOVERY_PENDING,
            RECOVERY_PENDING_NET_ID + "," + RECOVERY_PENDING,
            LOST_NET_ID + "," + LOST,
            RECOVERED_NET_ID + "," + RECOVERED
    })
    @WithMockUser(username = "standard-user", roles = {STANDARD_ROLE})
    void shouldUpdateState_whenLoggedIn_onMarkNetRecovered(String netId, String expectedStatus) throws Exception {
        sendPostRequestAndExpectRedirectToNetsPage(mockMvc, String.format(REQUEST_NET_RECOVERY_ENDPOINT, Long.valueOf(netId)));
        Document doc = sendGetRequestToNetsPage(mockMvc);
        assertExpectedNetState_forNetId_onNetsPage(doc, netId, expectedStatus);
    }

    @Test
    @WithMockUser(username = "standard-user", roles = {STANDARD_ROLE})
    void shouldShowToastError_whenWrongId_onRequestNetRecovery() throws Exception {
        MvcResult requestRecoveryResult = sendPostRequestAndExpectRedirectToNetsPage(mockMvc, REQUEST_NET_RECOVERY_ENDPOINT.replace("%d", INVALID_NET_ID));

        MockHttpSession session = getSession(requestRecoveryResult);
        Document doc = sendGetRequestToNetsPage(mockMvc, session);

        assertToastMessageExists(doc, INVALID_ID_TOAST_MESSAGE);
    }

    @Test
    @WithMockUser(roles = {STANDARD_ROLE})
    void shouldShowToastError_WhenNetIdNotFound_onRequestNetRecovery() throws Exception {
        MvcResult result = sendPostRequestAndExpectRedirectToNetsPage(mockMvc, String.format(REQUEST_NET_RECOVERY_ENDPOINT, Long.valueOf(NOT_EXISTING_NET_ID)));
        MockHttpSession session = getSession(result);
        Document doc = sendGetRequestToNetsPage(mockMvc, session);
        assertToastMessageExists(doc, ID_NOT_FOUND_TOAST_MESSAGE);
    }

    @ParameterizedTest
    @ValueSource(strings = {RECOVERED_NET_ID, LOST_NET_ID, RECOVERY_PENDING_NET_ID})
    @WithMockUser(roles = {STANDARD_ROLE})
    void shouldShowToastError_WhenIllegalNetStateChange_onRequestNetRecovery(String netId) throws Exception {
        MvcResult result = sendPostRequestAndExpectRedirectToNetsPage(mockMvc, String.format(REQUEST_NET_RECOVERY_ENDPOINT, Long.valueOf(netId)));
        MockHttpSession session = getSession(result);
        Document doc = sendGetRequestToNetsPage(mockMvc, session);
        assertToastMessageExists(doc, ILLEGAL_STATE_CHANGE_TOAST_MESSAGE);
    }
}
