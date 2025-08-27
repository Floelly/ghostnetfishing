package dev.floelly.ghostnetfishing.integration.flow;

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
        // TODO: refactor!
    void shouldSaveGhostNetAndRedirectToOverview_OnPostNewNet() throws Exception {
        String randomLatitude = getRandomLatitude();
        String randomLongitude = getRandomLongitude();
        String randomSize = getRandomNetSize();

        assertPostNewNetSuccessful(mockMvc, randomLatitude, randomLongitude, randomSize);

        mockMvc.perform(get(NETS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(randomLatitude)))
                .andExpect(content().string(containsString(randomLongitude)))
                .andExpect(content().string(containsString(randomSize)))
                .andExpect(content().string(containsString(REPORTED)));
    }

    @Test
        // TODO: refactor!
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
    // TODO: refactor!
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
        MvcResult postResult = assertPostNewNetSuccessful(mockMvc, getRandomLatitude(), getRandomLongitude(), getRandomNetSize());

        MockHttpSession session = getSession(postResult);
        Document doc = sendGetRequestToNetsPage(mockMvc, session);

        assertToastMessageExists(doc, SUCCESSFULLY_REPORTED_NET_MESSAGE);
    }
}
