package dev.floelly.ghostnetfishing.integration.renderingAndFlow;

import dev.floelly.ghostnetfishing.testutil.AbstractH2Test;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Sql(scripts = "/sql/populate-nets-table-diverse.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class NetsLayoutRenderingTest extends AbstractH2Test {
    public static final String REPORTED_NET_ID = "1001";
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = {STANDARD_ROLE})
    void shouldRenderRequestRecoveryButtonWithCorrectId_whenLoggedIn_onNetsPage() throws Exception {
        MvcResult result = mockMvc.perform(get(NETS_ENDPOINT))
                .andReturn();
        Document document = Jsoup.parse(result.getResponse().getContentAsString());
        Element matchingRow = document.select("main tbody tr[data-net-id=" + REPORTED_NET_ID + "]").stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("No Net found for lat/lon"));

        String id = matchingRow.attr("data-net-id");
        assertThat(id).isNotEmpty();

        Element form = matchingRow.selectFirst("form[method=post][action$=/request-recovery]");
        assertNotNull(form);
        assertThat(form.attr("action")).isEqualTo(String.format(REQUEST_NET_RECOVERY_ENDPOINT, Long.valueOf(id)));

        Element button = form.selectFirst("button[type=submit]");
        assertNotNull(button);
    }
}
