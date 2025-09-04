package dev.floelly.ghostnetfishing.integration.rendering;

import dev.floelly.ghostnetfishing.testutil.AbstractH2Test;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static dev.floelly.ghostnetfishing.testutil.FrontEndTestFunctions.*;
import static dev.floelly.ghostnetfishing.testutil.MvcTestFunctions.sendGetRequestToNetsPage;
import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = {"/sql/populate-default-user.sql", "/sql/populate-nets-table-diverse.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
//TODO: More tests for Layout and Accessibility
public class NetsPagePublicRenderingTest extends AbstractH2Test {

    private Element reportedNetRow;
    private Element recoveryPendingNetRow;
    private Element recoveredNetRow;
    private Element lostNetRow;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public void setUp() throws Exception {
        Document document = sendGetRequestToNetsPage(mockMvc);
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

    @ParameterizedTest
    @MethodSource("rowProvider")
    void shouldNotRenderMarkLostButton_whenNotLoggedIn_onNetsPage(Element row) {
        Element form = row.selectFirst(MARK_LOST_FORM_QUERY);
        assertNull(form);
    }

    @Test
    void shouldNotShowRecoverer_whenNotLoggedIn_onNetsPage() {
        assertThat(recoveryPendingNetRow.text()).doesNotContain(USERNAME_WITH_NUMBER_AND_NET);
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
