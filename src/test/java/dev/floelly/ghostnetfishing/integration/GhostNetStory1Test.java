package dev.floelly.ghostnetfishing.integration;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GhostNetStory1Test {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldDisplayGhostNetForm() throws Exception {
        mockMvc.perform(get("/nets/new"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Neues Geisternetz melden")))
                .andExpect(content().string(containsString("Standort")))
                .andExpect(content().string(containsString("Breitengrad")))
                .andExpect(content().string(containsString("Längengrad")))
                .andExpect(content().string(containsString("Größe")));
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
