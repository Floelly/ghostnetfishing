package dev.floelly.ghostnetfishing.integration.renderingAndFlow;

import dev.floelly.ghostnetfishing.testutil.AbstractH2Test;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;

public class PostNewNetFlowTest extends AbstractH2Test {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldSaveGhostNetAndRedirectToOverview_OnPostNewNet() throws Exception {
        double lat = getRandomLatitude();
        double lon = getRandomLongitude();
        String randomLatitude = formatDouble(lat);
        String randomLongitude = formatDouble(lon);

        mockMvc.perform(post(NETS_NEW_ENDPOINT)
                        .param(LOCATION_LAT, randomLatitude)
                        .param(LOCATION_LONG, randomLongitude)
                        .param(SIZE, "L")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(NETS_ENDPOINT));

        mockMvc.perform(get(NETS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(randomLatitude)))
                .andExpect(content().string(containsString(randomLongitude)))
                .andExpect(content().string(containsString("L")))
                .andExpect(content().string(containsString("REPORTED")));
    }

    @Test
    void shouldFailValidationAndStayOnForm_whenGivenInvalidValues_OnPostNewNet() throws Exception {
        mockMvc.perform(post(NETS_NEW_ENDPOINT)
                        .param(LOCATION_LAT, EMPTY_STRING)
                        .param(LOCATION_LONG, EMPTY_STRING)
                        .param(SIZE, EMPTY_STRING)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors(NEW_NET_REQUEST, LOCATION_LAT, LOCATION_LONG, SIZE))
                .andExpect(view().name(NETS_NEW_ENDPOINT));
    }

    @Test
    void shouldDisplayErrorInformation_whenGivenInvalidValues_OnPostNewNet() throws Exception {
        String content = mockMvc.perform(post(NETS_NEW_ENDPOINT)
                        .param(LOCATION_LAT, WRONG_NET_LOCATION_LAT)
                        .param(LOCATION_LONG, WRONG_NET_LOCATION_LONG)
                        .param(SIZE, WRONG_NET_SIZE)
                        .with(csrf()))
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
    void shouldDisplaySuccessInfoToastMessage_onPostNewNet() throws Exception {
        double lat = getRandomLatitude();
        double lon = getRandomLongitude();
        String randomLatitude = formatDouble(lat);
        String randomLongitude = formatDouble(lon);

        MvcResult postResult = mockMvc.perform(post(NETS_NEW_ENDPOINT)
                        .param(LOCATION_LAT, randomLatitude)
                        .param(LOCATION_LONG, randomLongitude)
                        .param(SIZE, "L")
                        .with(csrf()))
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
