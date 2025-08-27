package dev.floelly.ghostnetfishing.integration.renderingAndFlow;

import dev.floelly.ghostnetfishing.testutil.AbstractH2Test;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql(scripts = "/sql/populate-nets-table-diverse.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class MarkNetRecoveredFlowTest extends AbstractH2Test {
    public static final String RECOVERED = "RECOVERED";
    public static final String RECOVERY_PENDING_ID = "1004";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "standard-user", roles = {STANDARD_ROLE})
    void shouldChangeStateInFrontEnd_whenSuccessful_onRequestNetRecovery() throws Exception {
        //request recovery for net 1001 (see prepopulated table
        MvcResult requestRecoveryResult = mockMvc.perform(post(String.format(MARK_NET_RECOVERED_ENDPOINT, Long.valueOf(RECOVERY_PENDING_ID)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(NETS_ENDPOINT))
                .andReturn();
        String redirectedUrlOnRequestRecovery = requestRecoveryResult.getResponse().getRedirectedUrl();
        assertNotNull(redirectedUrlOnRequestRecovery);

        //check recovered status on net
        MvcResult finalResult = mockMvc.perform(get(redirectedUrlOnRequestRecovery))
                .andExpect(status().isOk())
                .andReturn();
        Document finalDoc = Jsoup.parse(finalResult.getResponse().getContentAsString());

        Elements rows = finalDoc.select(TABLE_ROWS_QUERY_SELECTOR);
        Element row = rows.selectFirst(String.format(NET_ID_TR_QUERY, RECOVERY_PENDING_ID));
        assertNotNull(row);
        assertThat(row.text()).as(String.format("Cannot find net status '%s' in table row. Given: '%s", RECOVERED, row.text())).contains(RECOVERED);
    }

    @Disabled("not Implemented jet")
    @Test
    void shouldShowToastError_whenWrongId_onRequestNetRecovery()  {

    }

    @Disabled("not Implemented jet")
    @Test
    void shouldShowToastError_WhenNetIdNotFound_onRequestNetRecovery() {
        //TODO: implement. functionality already implemented
    }

    @Disabled("not Implemented jet")
    @Test
    void shouldShowToastError_WhenIllegalNetStateChange_onRequestNetRecovery() {
        //TODO: implement. functionality already implemented
    }
}
