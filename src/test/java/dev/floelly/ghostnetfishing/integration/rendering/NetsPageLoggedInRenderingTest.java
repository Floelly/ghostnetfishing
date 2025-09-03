package dev.floelly.ghostnetfishing.integration.rendering;

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

import static dev.floelly.ghostnetfishing.testutil.FrontEndTestFunctions.*;
import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = {"/sql/populate-default-user.sql", "/sql/populate-nets-table-diverse.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
//TODO: More tests for Layout and Accessibility
public class NetsPageLoggedInRenderingTest extends AbstractH2Test {

    private Element reportedNetRow;
    private Element recoveryPendingNetRow;
    private Element recoveredNetRow;
    private Element lostNetRow;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public void setUp() throws Exception {
        MvcResult result = mockMvc.perform(get(NETS_ENDPOINT)
                        .with(user("user").roles(SPRING_SECURITY_STANDARD_ROLE)))
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
    @MethodSource("requestRecoveryFormRowProvider")
    void shouldRenderRequestRecoveryButtonWithCorrectPostAction_whenLoggedIn_onNetsPage(Element row, String formAction, boolean disabled) {
        Element form = row.selectFirst(REQUEST_RECOVERY_FORM_QUERY);
        assertNotNull(form);
        assertThat(form.attr("action")).isEqualTo(formAction);
        assertContainsSubmitButton(form, disabled);
    }

    @ParameterizedTest
    @MethodSource("markRecoveredFormRowProvider")
    void shouldRenderMarkRecoveredButtonWithCorrectPostAction_whenLoggedIn_onNetsPage(Element row, String formAction, boolean disabled) {
        Element form = row.selectFirst(MARK_RECOVERED_FORM_QUERY);
        assertNotNull(form);
        assertThat(form.attr("action")).isEqualTo(formAction);
        assertContainsSubmitButton(form, disabled);
    }

    @ParameterizedTest
    @MethodSource("markLostFormRowProvider")
    void shouldRenderMarkLostButtonWithCorrectPostAction_whenLoggedIn_onNetsPage(Element row, String formAction, boolean disabled) {
        Element form = row.selectFirst(MARK_LOST_FORM_QUERY);
        assertNotNull(form);
        assertThat(form.attr("action")).isEqualTo(formAction);
        assertContainsSubmitButton(form, disabled);
    }

    public Stream<Arguments> requestRecoveryFormRowProvider() {
        return Stream.of(
                Arguments.of(reportedNetRow, String.format(REQUEST_NET_RECOVERY_ENDPOINT, Long.valueOf(REPORTED_NET_ID)), false),
                Arguments.of(recoveryPendingNetRow, String.format(REQUEST_NET_RECOVERY_ENDPOINT, Long.valueOf(RECOVERY_PENDING_NET_ID)), true),
                Arguments.of(recoveredNetRow, String.format(REQUEST_NET_RECOVERY_ENDPOINT, Long.valueOf(RECOVERED_NET_ID)), true),
                Arguments.of(lostNetRow, String.format(REQUEST_NET_RECOVERY_ENDPOINT, Long.valueOf(LOST_NET_ID)), true)
        );
    }
    public Stream<Arguments> markRecoveredFormRowProvider() {
        return Stream.of(
                Arguments.of(reportedNetRow, String.format(MARK_NET_RECOVERED_ENDPOINT, Long.valueOf(REPORTED_NET_ID)), true),
                Arguments.of(recoveryPendingNetRow, String.format(MARK_NET_RECOVERED_ENDPOINT, Long.valueOf(RECOVERY_PENDING_NET_ID)), false),
                Arguments.of(recoveredNetRow, String.format(MARK_NET_RECOVERED_ENDPOINT, Long.valueOf(RECOVERED_NET_ID)), true),
                Arguments.of(lostNetRow, String.format(MARK_NET_RECOVERED_ENDPOINT, Long.valueOf(LOST_NET_ID)), true)
        );
    }

    public Stream<Arguments> markLostFormRowProvider() {
        return Stream.of(
                Arguments.of(reportedNetRow, String.format(MARK_NET_LOST_ENDPOINT, Long.valueOf(REPORTED_NET_ID)), false),
                Arguments.of(recoveryPendingNetRow, String.format(MARK_NET_LOST_ENDPOINT, Long.valueOf(RECOVERY_PENDING_NET_ID)), false),
                Arguments.of(recoveredNetRow, String.format(MARK_NET_LOST_ENDPOINT, Long.valueOf(RECOVERED_NET_ID)), true),
                Arguments.of(lostNetRow, String.format(MARK_NET_LOST_ENDPOINT, Long.valueOf(LOST_NET_ID)), true)
        );
    }
}
