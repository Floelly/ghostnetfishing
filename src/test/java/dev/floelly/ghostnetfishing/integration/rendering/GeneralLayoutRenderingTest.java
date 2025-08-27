package dev.floelly.ghostnetfishing.integration.rendering;

import dev.floelly.ghostnetfishing.testutil.AbstractH2Test;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;

//TODO: More tests for Layout and Accessibility
public class GeneralLayoutRenderingTest extends AbstractH2Test {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldLoadSpringContext() {

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
    void shouldNotDisplayAnyToastMessageByDefault_onNetsPage() throws Exception {
        MvcResult result = mockMvc.perform(get(NETS_ENDPOINT))
                .andExpect(status().isOk())
                .andReturn();
        Document doc = Jsoup.parse(result.getResponse().getContentAsString());
        Element toastContainer = doc.selectFirst(".toast-container");

        assertThat(toastContainer).isNotNull();

        Elements toastMessages = toastContainer.select(".toast");

        assertThat(toastMessages).isEmpty();
    }
}
