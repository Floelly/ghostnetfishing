package dev.floelly.ghostnetfishing.testutil;

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.shaded.org.checkerframework.checker.nullness.qual.Nullable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class TestDataFactory {
    public static final String NEW_NET_REQUEST = "newNetRequest";
    public static final String LOCATION_LAT = "locationLat";
    public static final String LOCATION_LONG = "locationLong";
    public static final String SIZE = "size";
    public static final List<String> NEW_NET_PARAMETERS = List.of(LOCATION_LAT, LOCATION_LONG, SIZE);
    public static final String EMPTY_STRING = "";
    public static final String WRONG_NET_LOCATION_LAT = "91.0";
    public static final String WRONG_NET_LOCATION_LONG = "-180.1";
    public static final String WRONG_NET_SIZE = "XXS";
    public static final String NETS_NEW_ENDPOINT = "/nets/new";
    public static final String NETS_ENDPOINT = "/nets";
    public static final String LOGIN_ENDPOINT = "/login";
    public static final String NEW_GHOST_NET_HEADLINE = "Report new ghost net";
    public static final String SUCCESSFULLY_REPORTED_NET_MESSAGE = "New net added successfully";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    public static final String LAYOUT_HTML_TAG = "footer";
    public static final Map<String, String> EXPECTED_SIZE_OPTIONS = Map.of(
            "", "- Choose size -",
            "S", "S - (Diameter up to 10 m)",
            "M", "M - (Diameter up to 30 m)",
            "L", "L - (Diameter up to 100 m)",
            "XL", "XL - (Diameter over 100 m)"
    );
    public static final String DB_COLUMN_LATITUDE = "location_lat";
    public static final String DB_COLUMN_LONGITUDE = "location_long";
    public static final String DB_COLUMN_SIZE = "size";
    public static final String DB_COLUMN_NETS = "nets";

    public static final String STANDARD_ROLE = "STANDARD";

    public static final String REQUEST_NET_RECOVERY_ENDPOINT = "/nets/%d/request-recovery";
    public static final String MARK_NET_RECOVERED_ENDPOINT = "/nets/%d/mark-recovered";
    public static final String MARK_NET_LOST_ENDPOINT = "/nets/%d/mark-lost";

    public static final String NEW_NET_FORM_QUERY_SELECTOR = "form[method=post][action='" + NETS_NEW_ENDPOINT + "']";
    public static final String INVALID_FEEDBACK_QUERY_SELECTOR = ".invalid-feedback";
    public static final String MARK_RECOVERED_FORM_QUERY = "form[method=post][action$=/mark-recovered]";
    public static final String REQUEST_RECOVERY_FORM_QUERY = "form[method=post][action$=/request-recovery]";
    public static final String SUBMIT_BUTTON_QUERY = "button[type=submit]";
    public static final String NET_ID_TR_QUERY = "tr[data-net-id=%s]";
    public static final String TABLE_ROWS_QUERY_SELECTOR = "main tbody tr";
    public static final String MARK_LOST_FORM_QUERY = "form[method=post][action$=/mark-lost]";
    public static final String TOAST_QUERY = ".toast-container .toast";


    public static final String LOST = "LOST";
    public static final String RECOVERED = "RECOVERED";
    public static final String RECOVERY_PENDING = "RECOVERY_PENDING";
    public static final String REPORTED = "REPORTED";
    public static final String REPORTED_ID = "1001";
    public static final String LOST_ID = "1002";
    public static final String RECOVERED_ID = "1003";
    public static final String RECOVERY_PENDING_ID = "1004";
    public static final String INVALID_NET_ID = "invalidNetId";

    public static String formatDouble(double d) {
        return getDoubleFormat().format(d);
    }

    public static DecimalFormat getDoubleFormat() {
        DecimalFormat df = new DecimalFormat("#.####");
        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
        df.setDecimalSeparatorAlwaysShown(false);
        return df;
    }

    public static double getRandomLongitude() {
        return ThreadLocalRandom.current().nextDouble(-180, 180);
    }

    public static double getRandomLatitude() {
        return ThreadLocalRandom.current().nextDouble(-90, 90);
    }

    public static MvcResult sendPostRequestAndExpectRedirectToNetsPage(MockMvc mockMvc, String url) throws Exception {
        return mockMvc.perform(post(url)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(NETS_ENDPOINT))
                .andReturn();
    }

    public static Document sendGetRequestToNetsPage(MockMvc mockMvc) throws Exception {
        return sendGetRequestToNetsPage(mockMvc, null);
    }
    public static Document sendGetRequestToNetsPage(MockMvc mockMvc, @Nullable MockHttpSession session) throws Exception {
        MvcResult result = performSuccessfulGet(mockMvc, NETS_ENDPOINT, session);
        return Jsoup.parse(result.getResponse().getContentAsString());
    }

    private static MvcResult performSuccessfulGet(MockMvc mockMvc, String url, @Nullable MockHttpSession session) throws Exception {
        var builder = Objects.isNull(session) ? get(url) : get(url).session(session);
        return mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn();
    }

    public static void assertExpectedNetState_forNetId_onNetsPage(Document doc, String netId, String expectedState)  {
        Elements rows = doc.select(TABLE_ROWS_QUERY_SELECTOR);
        Element row = rows.selectFirst(String.format(NET_ID_TR_QUERY, netId));
        assertNotNull(row);
        assertThat(row.text())
                .as(String.format("Cannot find net status '%s' in table row. Given: '%s", expectedState, row.text()))
                .contains(expectedState);
    }

    public static @NotNull MockHttpSession getSession(MvcResult requestRecoveryResult) {
        MockHttpSession session = (MockHttpSession) requestRecoveryResult.getRequest().getSession(false);
        assertNotNull(session);
        return session;
    }

    public static void assertToastMessageExists(Document doc, String... expectedStrings) {
        Elements toastMessages = doc.select(TOAST_QUERY);
        List<String> toastMessageStrings = toastMessages.stream().map(Element::text).toList();

        for (String expectedString : expectedStrings) {
            AssertionsForInterfaceTypes.assertThat(toastMessageStrings).anySatisfy(s -> assertThat(s).contains(expectedString));
        }
    }
}
