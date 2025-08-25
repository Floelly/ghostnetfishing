package dev.floelly.ghostnetfishing.integration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest()
@AutoConfigureMockMvc
public class GhostNetStory1Test {

    public static final String NEW_NET_REQUEST = "newNetRequest";
    public static final String LOCATION_LAT = "locationLat";
    public static final String LOCATION_LONG = "locationLong";
    public static final String SIZE = "size";
    public static final List<String> NEW_NET_PARAMETERS = List.of(LOCATION_LAT, LOCATION_LONG, SIZE);
    public static final String EMPTY_STRING = "";
    private static final String WRONG_NET_LOCATION_LAT = "91.0";
    private static final String WRONG_NET_LOCATION_LONG = "-180.1";
    private static final String WRONG_NET_SIZE = "XXS";
    public static final String NETS_NEW_ENDPOINT = "/nets/new";
    public static final String NETS_ENDPOINT = "/nets";
    public static final String NEW_GHOST_NET_HEADLINE = "Report new ghost net";
    public static final String SUCCESSFULLY_REPORTED_NET_MESSAGE = "New net added successfully";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    public static final String LAYOUT_HTML_TAG = "footer";
    public static final String NEW_NET_FORM_QUERY_SELECTOR = "form[method=post][action='" + NETS_NEW_ENDPOINT + "']";
    public static final String INVALID_FEEDBACK_QUERY_SELECTOR = ".invalid-feedback";
    private static final Map<String, String> EXPECTED_SIZE_OPTIONS = Map.of(
            "", "- Choose size -",
            "S", "S - (Diameter up to 10 m)",
            "M", "M - (Diameter up to 30 m)",
            "L", "L - (Diameter up to 100 m)",
            "XL", "XL - (Diameter over 100 m)"
    );

    private static String formatDouble(double d) {
        return getDoubleFormat().format(d);
    }

    private static DecimalFormat getDoubleFormat() {
        DecimalFormat df = new DecimalFormat("#.####");
        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
        df.setDecimalSeparatorAlwaysShown(false);
        return df;
    }

    private static double getRandomLongitude() {
        return ThreadLocalRandom.current().nextDouble(-180, 180);
    }

    private static double getRandomLatitude() {
        return ThreadLocalRandom.current().nextDouble(-90, 90);
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldLoadSpringContext() {
    }

    @Test
    void shouldDisplayGhostNetFormWithCorrectFieldsAndButton_OnGetNewNetForm() throws Exception {
        MvcResult result = mockMvc.perform(get(NETS_NEW_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(NEW_GHOST_NET_HEADLINE)))
                .andExpect(content().string(containsString(LATITUDE)))
                .andExpect(content().string(containsString(LONGITUDE)))
                .andExpect(content().string(containsString(SIZE)))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Document document = Jsoup.parse(content);

        Elements form = document.select(NEW_NET_FORM_QUERY_SELECTOR);
        assertThat(form)
                .withFailMessage("No Form element with post method found.")
                .isNotEmpty();

        List<String> inputs = form.select("input, select").stream()
                .map(e -> e.attr("name"))
                .toList();
        assertThat(inputs)
                .withFailMessage("Expects inputs or selects with names '%s'. Found: '%s'", String.join(", ", NEW_NET_PARAMETERS), String.join(", ", inputs))
                .containsAll(NEW_NET_PARAMETERS);

        Elements submitButtons = form.select("button[type=submit], input[type=submit]");
        assertThat(submitButtons)
                .withFailMessage("Expects at least one submit button or submit input field. Found '%s'.", submitButtons.size())
                .isNotEmpty();
    }

    @Test
    void shouldDisplayCorrectSelectNode_OnGetNewNetForm() throws Exception {
        String content = mockMvc.perform(get(NETS_NEW_ENDPOINT))
                .andReturn()
                .getResponse()
                .getContentAsString();
        Elements form = Jsoup.parse(content).select(NEW_NET_FORM_QUERY_SELECTOR);
        Element locationSizeElement = form.selectFirst("select[name="+SIZE+"]");

        assertThat(locationSizeElement).isNotNull();
        assertThat(locationSizeElement.hasAttr("required")).isTrue();

        Elements options = locationSizeElement.select("option");

        for (Element option : options) {
            String value = option.attr("value");
            if(value.equals(EMPTY_STRING)) {
                assertThat(option.hasAttr("disabled")).isTrue();
                assertThat(option.hasAttr("selected")).isTrue();
            }
            assertThat(EXPECTED_SIZE_OPTIONS).containsKey(value);
            assertThat(option.text()).isEqualTo(EXPECTED_SIZE_OPTIONS.get(value));
        }
    }

    @Test
    void shouldDisplayCorrectInputNodes_OnGetNewNetForm() throws Exception {
        String content = mockMvc.perform(get(NETS_NEW_ENDPOINT))
                .andReturn()
                .getResponse()
                .getContentAsString();
        Elements form = Jsoup.parse(content).select(NEW_NET_FORM_QUERY_SELECTOR);
        Element locationLatElement = form.selectFirst("input[name="+LOCATION_LAT+"]");
        Element locationLongElement = form.selectFirst("input[name="+LOCATION_LONG+"]");

        assertThat(locationLatElement).isNotNull();
        assertThat(locationLatElement.hasAttr("required")).isTrue();
        assertThat(locationLatElement.attr("type")).isEqualTo("number");
        assertThat(locationLatElement.attr("min")).isEqualTo("-90");
        assertThat(locationLatElement.attr("max")).isEqualTo("90");
        assertThat(locationLatElement.attr("step")).isEqualTo("0.0001");

        assertThat(locationLongElement).isNotNull();
        assertThat(locationLongElement.hasAttr("required")).isTrue();
        assertThat(locationLongElement.attr("type")).isEqualTo("number");
        assertThat(locationLongElement.attr("min")).isEqualTo("-180");
        assertThat(locationLongElement.attr("max")).isEqualTo("180");
        assertThat(locationLongElement.attr("step")).isEqualTo("0.0001");
    }

    @Test
    void shouldRenderContentInLayout_OnGetNewNetForm() throws Exception {
        MvcResult result = mockMvc.perform(get(NETS_NEW_ENDPOINT))
                .andExpect(status().isOk())
                .andReturn();

        Document doc = Jsoup.parse(result.getResponse().getContentAsString());
        assertThat(doc.select(LAYOUT_HTML_TAG)).isNotEmpty();
    }

    @Test
    void shouldSaveGhostNetAndRedirectToOverview_OnPostNewNet() throws Exception {
        double lat = getRandomLatitude();
        double lon = getRandomLongitude();
        String randomLatitude = formatDouble(lat);
        String randomLongitude = formatDouble(lon);

        mockMvc.perform(post(NETS_NEW_ENDPOINT)
                    .param(LOCATION_LAT, randomLatitude)
                    .param(LOCATION_LONG, randomLongitude)
                    .param(SIZE, "L"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(NETS_ENDPOINT));

        mockMvc.perform(get(NETS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(randomLatitude)))
                .andExpect(content().string(containsString(randomLongitude)));
    }

    @Test
    void shouldFailValidationAndStayOnForm_whenGivenInvalidValues_OnPostNewNet() throws Exception {
        mockMvc.perform(post(NETS_NEW_ENDPOINT)
                        .param(LOCATION_LAT, EMPTY_STRING)
                        .param(LOCATION_LONG, EMPTY_STRING)
                        .param(SIZE, EMPTY_STRING))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors(NEW_NET_REQUEST, LOCATION_LAT, LOCATION_LONG, SIZE))
                .andExpect(view().name(NETS_NEW_ENDPOINT));
    }

    @Test
    void shouldDisplayErrorInformation_whenGivenInvalidValues_OnPostNewNet() throws Exception {
        String content = mockMvc.perform(post(NETS_NEW_ENDPOINT)
                        .param(LOCATION_LAT, WRONG_NET_LOCATION_LAT)
                        .param(LOCATION_LONG, WRONG_NET_LOCATION_LONG)
                        .param(SIZE, WRONG_NET_SIZE))
                .andReturn()
                .getResponse()
                .getContentAsString();
        Elements form = Jsoup.parse(content).select(NEW_NET_FORM_QUERY_SELECTOR);
        Elements errorDivs = form.select(INVALID_FEEDBACK_QUERY_SELECTOR);

        assertThat(errorDivs).size().isEqualTo(3);

        List<String> errorMessages = errorDivs.stream().map(Element::text).collect(Collectors.toList());

        assertThat(errorMessages).doesNotContain(EMPTY_STRING);
    }

    @Test
    void shouldNotDisplayAnyToastMessage_onNetsPage() throws Exception {
        MvcResult result = mockMvc.perform(get(NETS_ENDPOINT))
                .andExpect(status().isOk())
                .andReturn();
        Document doc = Jsoup.parse(result.getResponse().getContentAsString());
        Element toastContainer = doc.selectFirst(".toast-container");

        assertThat(toastContainer).isNotNull();

        Elements toastMessages = toastContainer.select(".toast");

        assertThat(toastMessages).isEmpty();
    }

    @Test
    void shouldDisplaySuccessInfoToastMessage_onPostNewNet() throws Exception {
        double lat = getRandomLatitude();
        double lon = getRandomLongitude();
        String randomLatitude = formatDouble(lat);
        String randomLongitude = formatDouble(lon);

        MvcResult postResult = mockMvc.perform(post(NETS_NEW_ENDPOINT)
                        .param(LOCATION_LAT, randomLatitude)
                        .param(LOCATION_LONG, randomLongitude)
                        .param(SIZE, "L"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(NETS_ENDPOINT))
                .andExpect(flash().attributeExists("toastMessages"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) postResult.getRequest().getSession(false);

        assertThat(session).isNotNull();

        MvcResult getResult = mockMvc.perform(get(NETS_ENDPOINT).session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("toastMessages"))
                .andReturn();
        Document doc = Jsoup.parse(getResult.getResponse().getContentAsString());
        Element toastContainer = doc.selectFirst(".toast-container");
        assertThat(toastContainer).isNotNull();
        Elements toastMessages = toastContainer.select(".toast");

        assertThat(toastMessages).size().isEqualTo(1);

        String toastHtml = Objects.requireNonNull(toastMessages.first()).toString();

        assertThat(toastHtml).contains(SUCCESSFULLY_REPORTED_NET_MESSAGE);
    }
}
