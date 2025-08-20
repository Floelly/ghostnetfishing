package dev.floelly.ghostnetfishing.integration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
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
    void shouldDisplayGhostNetForm() throws Exception {
        List<String> newNetParameters = List.of("locationLong", "locationLat", "size");

        MvcResult result = mockMvc.perform(get("/nets/new"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Neues Geisternetz melden")))
                .andExpect(content().string(containsString("Breitengrad")))
                .andExpect(content().string(containsString("Längengrad")))
                .andExpect(content().string(containsString("Größe")))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Document document = Jsoup.parse(content);

        Elements form = document.select("form[method=post]");
        assertThat(form)
                .withFailMessage("No Form element found")
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
    void shouldRenderContentInLayout() throws Exception {
        MvcResult result = mockMvc.perform(get("/nets/new"))
                .andExpect(status().isOk())
                .andReturn();

        Document doc = Jsoup.parse(result.getResponse().getContentAsString());
        assertThat(doc.select("footer")).isNotEmpty();
    }

    @Disabled("Noch nicht implementiert")
    @Test
    void shouldSaveGhostNetAndRedirectToOverview() throws Exception {
        double lat = ThreadLocalRandom.current().nextDouble(-90, 90);
        double lon = ThreadLocalRandom.current().nextDouble(-180, 180);
        String randomLatitude = String.format("%.4f", lat);
        String randomLongitude = String.format("%.4f", lon);

        mockMvc.perform(post("/nets/new")
                    .param("locationLong", randomLongitude)
                    .param("locationLat", randomLatitude)
                    .param("size", "L"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/nets"));

        mockMvc.perform(get("/nets"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(randomLatitude)))
                .andExpect(content().string(containsString(randomLongitude)));
    }
}
