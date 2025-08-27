package dev.floelly.ghostnetfishing.integration.renderingAndFlow;

import dev.floelly.ghostnetfishing.testutil.AbstractH2Test;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.stream.Stream;

import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = "/sql/populate-nets-table-diverse.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class NetsPagePublicRenderingTest extends AbstractH2Test {
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
        MvcResult result = mockMvc.perform(get(NETS_ENDPOINT))
                .andReturn();
        Document document = Jsoup.parse(result.getResponse().getContentAsString());
        Elements rows = document.select(TABLE_ROWS_QUERY_SELECTOR);
        reportedNetRow = rows.selectFirst(String.format(NET_ID_TR_QUERY, REPORTED_NET_ID));
        recoveryPendingNetRow = rows.selectFirst(String.format(NET_ID_TR_QUERY, RECOVERY_PENDING_NET_ID));
        recoveredNetRow = rows.selectFirst(String.format(NET_ID_TR_QUERY, RECOVERED_NET_ID));
        lostNetRow = rows.selectFirst(String.format(NET_ID_TR_QUERY, LOST_NET_ID));
        assertNotNull(reportedNetRow);
        assertNotNull(recoveryPendingNetRow);
        assertNotNull(recoveredNetRow);
        assertNotNull(lostNetRow);
    }

    @ParameterizedTest
    @MethodSource("rowProvider")
    void shouldNotRenderRequestRecoveryButton_whenNotLoggedIn_onNetsPage(Element row) {
        Element form = row.selectFirst(REQUEST_RECOVERY_FORM_QUERY);
        assertNull(form);
    }

    @ParameterizedTest
    @MethodSource("rowProvider")
    void shouldNotRenderMarkRecoveredButton_whenNotLoggedIn_onNetsPage(Element row) {
        Element form = row.selectFirst(MARK_RECOVERED_FORM_QUERY);
        assertNull(form);
    }
    public Stream<Arguments> rowProvider() {
        return Stream.of(
                Arguments.of(reportedNetRow),
                Arguments.of(recoveryPendingNetRow),
                Arguments.of(recoveredNetRow),
                Arguments.of(lostNetRow)
        );
    }
}
