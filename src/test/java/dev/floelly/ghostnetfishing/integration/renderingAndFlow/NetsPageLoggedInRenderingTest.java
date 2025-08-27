package dev.floelly.ghostnetfishing.integration.renderingAndFlow;

import dev.floelly.ghostnetfishing.testutil.AbstractH2Test;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = "/sql/populate-nets-table-diverse.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class NetsPageLoggedInRenderingTest extends AbstractH2Test {
    public static final String REPORTED_NET_ID = "1001";
    public static final String RECOVERY_PENDING_NET_ID = "1004";
    public static final String RECOVERED_NET_ID = "1003";
    public static final String LOST_NET_ID = "1002";
    private Element reportedNetRow;
    private Element recoveryPendingNetRow;
    private Element recoveredNetRow;
    private Element lostNetRow;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public void setUp() throws Exception {
        MvcResult result = mockMvc.perform(get(NETS_ENDPOINT)
                        .with(user("user").roles(STANDARD_ROLE)))
                .andReturn();
        Document document = Jsoup.parse(result.getResponse().getContentAsString());
        Elements rows = document.select("main tbody tr");
        reportedNetRow = rows.selectFirst("tr[data-net-id="+REPORTED_NET_ID+"]");
        recoveryPendingNetRow = rows.selectFirst("tr[data-net-id="+RECOVERY_PENDING_NET_ID+"]");
        recoveredNetRow = rows.selectFirst("tr[data-net-id="+RECOVERED_NET_ID+"]");
        lostNetRow = rows.selectFirst("tr[data-net-id="+LOST_NET_ID+"]");
        assertNotNull(reportedNetRow);
        assertNotNull(recoveryPendingNetRow);
        assertNotNull(recoveredNetRow);
        assertNotNull(lostNetRow);
    }

    @Test
    void shouldRenderRequestRecoveryButtonWithCorrectPostMethod_whenLoggedIn_onNetsPage() throws Exception {
        Element form = reportedNetRow.selectFirst("form[method=post][action$=/request-recovery]");
        assertNotNull(form);
        assertThat(form.attr("action")).isEqualTo(String.format(REQUEST_NET_RECOVERY_ENDPOINT, Long.valueOf(REPORTED_NET_ID)));
        assertContainsActiveButton(form);
    }

    @Test
    void shouldRenderMarkRecoveredButtonWithCorrectId_whenLoggedIn_onNetsPage() throws Exception {
        Element form = reportedNetRow.selectFirst("form[method=post][action$=/mark-recovered]");
        assertNotNull(form);
        assertThat(form.attr("action")).isEqualTo(String.format(MARK_NET_RECOVERED_ENDPOINT, Long.valueOf(REPORTED_NET_ID)));
        assertContainsDisabledButton(form);
    }

    private static void assertContainsActiveButton(Element form) {
        assertContainsSubmitButton(form, false);
    }
    private static void assertContainsDisabledButton(Element form) {
        assertContainsSubmitButton(form, true);
    }
    private static void assertContainsSubmitButton(Element form, boolean disabled) {
        Element button = form.selectFirst("button[type=submit]");
        assertNotNull(button);
        assertThat(button.hasAttr("disabled")).isEqualTo(disabled);
    }
}
