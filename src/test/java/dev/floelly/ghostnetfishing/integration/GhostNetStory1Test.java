package dev.floelly.ghostnetfishing.integration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest()
@AutoConfigureMockMvc
public class GhostNetStory1Test {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldLoadSpringContext() {
    }

    @Test
    void shouldDisplayGhostNetForm_OnGetNewNetForm() throws Exception {
        List<String> newNetParameters = List.of("locationLat", "locationLong", "size");

        MvcResult result = mockMvc.perform(get("/nets/new"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Report new ghost net")))
                .andExpect(content().string(containsString("Latitude")))
                .andExpect(content().string(containsString("Longitude")))
                .andExpect(content().string(containsString("Size")))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Document document = Jsoup.parse(content);

        Elements form = document.select("form[method=post][action='/nets/new']");
        assertThat(form)
                .withFailMessage("No Form element with post method found.")
                .isNotEmpty();


        List<String> inputs = form.select("input, select").stream()
                .map(e -> e.attr("name"))
                .toList();
        assertThat(inputs)
                .withFailMessage("Expects inputs or selects with names '%s'. Found: '%s'", String.join(", ", newNetParameters), String.join(", ", inputs))
                .containsAll(newNetParameters);

        Elements submitButtons = form.select("button[type=submit], input[type=submit]");
        assertThat(submitButtons)
                .withFailMessage("Expects at least one submit button or submit input field. Found '%s'.", submitButtons.size())
                .isNotEmpty();
    }

    @Test
    void shouldRenderContentInLayout_OnGetNewNetForm() throws Exception {
        MvcResult result = mockMvc.perform(get("/nets/new"))
                .andExpect(status().isOk())
                .andReturn();

        Document doc = Jsoup.parse(result.getResponse().getContentAsString());
        assertThat(doc.select("footer")).isNotEmpty();
    }

    @Test
    void shouldSaveGhostNetAndRedirectToOverview_OnPostNewNet() throws Exception {
        double lat = ThreadLocalRandom.current().nextDouble(-90, 90);
        double lon = ThreadLocalRandom.current().nextDouble(-180, 180);
        DecimalFormat df = new DecimalFormat("#.####");
        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
        df.setDecimalSeparatorAlwaysShown(false);
        String randomLatitude = df.format(lat);
        String randomLongitude = df.format(lon);

        mockMvc.perform(post("/nets/new")
                    .param("locationLat", randomLatitude)
                    .param("locationLong", randomLongitude)
                    .param("size", "L"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/nets"));

        mockMvc.perform(get("/nets"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(randomLatitude)))
                .andExpect(content().string(containsString(randomLongitude)));
    }

    @Test
    void shouldFailValidationAndStayOnForm_whenGivenInvalidValues_OnPostNewNet() throws Exception {
        mockMvc.perform(post("/nets/new")
                        .param("locationLat", "")
                        .param("locationLong", "")
                        .param("size", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("newNetRequest", "locationLat", "locationLong", "size"))
                .andExpect(view().name("/nets/new"));
    }

    @Disabled("not implemented jet")
    @Test
    void shouldDisplayErrorMessagesInToast_whenGivenInvalidValues_OnPostNewNet() {

    }
}
