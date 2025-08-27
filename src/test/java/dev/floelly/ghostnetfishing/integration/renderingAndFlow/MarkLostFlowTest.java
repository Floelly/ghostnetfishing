package dev.floelly.ghostnetfishing.integration.renderingAndFlow;

import dev.floelly.ghostnetfishing.testutil.AbstractH2Test;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;
import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.NET_ID_TR_QUERY;
import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.TABLE_ROWS_QUERY_SELECTOR;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/populate-nets-table-diverse.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class MarkLostFlowTest extends AbstractH2Test {
    private static final String LOST = "LOST";
    private static final String RECOVERED = "RECOVERED";
    public static final String REPORTED_ID = "1001";
    private static final String LOST_ID = "1002";
    private static final String RECOVERED_ID = "1003";
    public static final String RECOVERY_PENDING_ID = "1004";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldChangeStateInFrontEnd_whenStateRecoveryPending_onMarkNetLost() throws Exception {
        // try state change from reported to lost
        mockMvc.perform(post(String.format(MARK_NET_LOST_ENDPOINT, Long.valueOf(RECOVERY_PENDING_ID)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(NETS_ENDPOINT))
                .andReturn();

        //check recovered status on net
        MvcResult finalResult = mockMvc.perform(get(NETS_ENDPOINT))
                .andExpect(status().isOk())
                .andReturn();
        Document finalDoc = Jsoup.parse(finalResult.getResponse().getContentAsString());
        Elements rows = finalDoc.select(TABLE_ROWS_QUERY_SELECTOR);
        Element row = rows.selectFirst(String.format(NET_ID_TR_QUERY, RECOVERY_PENDING_ID));
        assertNotNull(row);
        assertThat(row.text()).as(String.format("Cannot find net status '%s' in table row. Given: '%s", LOST, row.text())).contains(LOST);
    }

    @Test
    void shouldChangeStateInFrontEnd_whenStateReported_onMarkNetLost() throws Exception {
        // try state change from reported to lost
        mockMvc.perform(post(String.format(MARK_NET_LOST_ENDPOINT, Long.valueOf(REPORTED_ID)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(NETS_ENDPOINT))
                .andReturn();

        //check recovered status on net
        MvcResult finalResult = mockMvc.perform(get(NETS_ENDPOINT))
                .andExpect(status().isOk())
                .andReturn();
        Document finalDoc = Jsoup.parse(finalResult.getResponse().getContentAsString());
        Elements rows = finalDoc.select(TABLE_ROWS_QUERY_SELECTOR);
        Element row = rows.selectFirst(String.format(NET_ID_TR_QUERY, REPORTED_ID));
        assertNotNull(row);
        assertThat(row.text()).as(String.format("Cannot find net status '%s' in table row. Given: '%s", LOST, row.text())).contains(LOST);
    }

    @Test
    void shouldNotChangeStateInFrontEnd_whenStateLost_onMarkNetLost() throws Exception {
        // try state change from reported to lost
        mockMvc.perform(post(String.format(MARK_NET_LOST_ENDPOINT, Long.valueOf(LOST_ID)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(NETS_ENDPOINT))
                .andReturn();

        //check recovered status on net
        MvcResult finalResult = mockMvc.perform(get(NETS_ENDPOINT))
                .andExpect(status().isOk())
                .andReturn();
        Document finalDoc = Jsoup.parse(finalResult.getResponse().getContentAsString());
        Elements rows = finalDoc.select(TABLE_ROWS_QUERY_SELECTOR);
        Element row = rows.selectFirst(String.format(NET_ID_TR_QUERY, LOST_ID));
        assertNotNull(row);
        assertThat(row.text()).as(String.format("Cannot find net status '%s' in table row. Given: '%s", LOST, row.text())).contains(LOST);
    }

    @Test
    void shouldNotChangeStateInFrontEnd_whenStateRecovered_onMarkNetLost() throws Exception {
        // try state change from reported to lost
        mockMvc.perform(post(String.format(MARK_NET_LOST_ENDPOINT, Long.valueOf(RECOVERED_ID)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(NETS_ENDPOINT))
                .andReturn();

        //check recovered status on net
        MvcResult finalResult = mockMvc.perform(get(NETS_ENDPOINT))
                .andExpect(status().isOk())
                .andReturn();
        Document finalDoc = Jsoup.parse(finalResult.getResponse().getContentAsString());
        Elements rows = finalDoc.select(TABLE_ROWS_QUERY_SELECTOR);
        Element row = rows.selectFirst(String.format(NET_ID_TR_QUERY, RECOVERED_ID));
        assertNotNull(row);
        assertThat(row.text()).as(String.format("Cannot find net status '%s' in table row. Given: '%s", RECOVERED, row.text())).contains(RECOVERED);
    }



    @Disabled("not Implemented jet")
    @Test
    void shouldShowToastError_whenWrongId_onMarkNetLost() throws Exception {
        //TODO: implement. functionality already implemented
    }

    @Disabled("not Implemented jet")
    @Test
    void shouldShowToastError_WhenNetIdNotFound_onMarkNetLost() {
        //TODO: implement. functionality already implemented
    }

    @Disabled("not Implemented jet")
    @Test
    void shouldShowToastError_WhenIllegalNetStateChange_onMarkNetLost() {
        //TODO: implement. functionality already implemented
    }
}
