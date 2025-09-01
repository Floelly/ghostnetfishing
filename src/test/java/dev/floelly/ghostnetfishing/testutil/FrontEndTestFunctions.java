package dev.floelly.ghostnetfishing.testutil;

import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;

public final class FrontEndTestFunctions {

    public static final String NEW_NET_FORM_QUERY_SELECTOR = "form[method=post][action='" + NETS_NEW_ENDPOINT + "']";
    public static final String INVALID_FEEDBACK_QUERY_SELECTOR = ".invalid-feedback";
    public static final String MARK_RECOVERED_FORM_QUERY = "form[method=post][action$=/mark-recovered]";
    public static final String REQUEST_RECOVERY_FORM_QUERY = "form[method=post][action$=/request-recovery]";
    public static final String SUBMIT_BUTTON_QUERY = "button[type=submit]";
    public static final String NET_ID_TR_QUERY = "tr[data-net-id=%s]";
    public static final String TABLE_ROWS_QUERY_SELECTOR = "main tbody tr";
    public static final String MARK_LOST_FORM_QUERY = "form[method=post][action$=/mark-lost]";
    public static final String TOAST_QUERY = ".toast-container .toast";

    public static void assertExpectedNetState_forNetId_onNetsPage(Document doc, String netId, String expectedState) {
        Elements rows = doc.select(TABLE_ROWS_QUERY_SELECTOR);
        Element row = rows.selectFirst(String.format(NET_ID_TR_QUERY, netId));
        Assertions.assertNotNull(row);
        AssertionsForClassTypes.assertThat(row.text())
                .as(String.format("Cannot find net status '%s' in table row. Given: '%s", expectedState, row.text()))
                .contains(expectedState);
    }

    public static void assertToastMessageExists(Document doc, String... expectedStrings) {
        Elements toastMessages = doc.select(TOAST_QUERY);
        List<String> toastMessageStrings = toastMessages.stream().map(Element::text).toList();

        for (String expectedString : expectedStrings) {
            AssertionsForInterfaceTypes.assertThat(toastMessageStrings).anySatisfy(s -> AssertionsForClassTypes.assertThat(s).contains(expectedString));
        }
    }

    public static void assertContainsSubmitButton(Element form, boolean disabled) {
        Element button = form.selectFirst(SUBMIT_BUTTON_QUERY);
        Assertions.assertNotNull(button);
        AssertionsForClassTypes.assertThat(button.hasAttr("disabled")).isEqualTo(disabled);
    }
}