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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        int amountOfInputs = 2;

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

        Elements inputs = form.select("input");
        assertThat(inputs.size())
                .withFailMessage("Expects at least %d input fields, actual %d. Form HTML:\n%s",
                        amountOfInputs,
                        inputs.size(),
                        form.html())
                .isGreaterThan(amountOfInputs - 1);
    }

    @Disabled("Noch nicht implementiert")
    @Test
    void shouldSaveGhostNetAndRedirectToOverview() throws Exception {
        mockMvc.perform(get("/nets/new")
                    .param("location_long", "8.990912")
                    .param("location_lat", "49.655653")
                    .param("size", "L"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/nets"));

        mockMvc.perform(get("/nets"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("49.655653")));
    }
}
