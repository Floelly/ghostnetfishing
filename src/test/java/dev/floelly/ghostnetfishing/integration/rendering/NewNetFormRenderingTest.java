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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;

public class NewNetFormRenderingTest extends AbstractH2Test {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldLoadSpringContext() {

    }

    @Test
    // TODO: refactor
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
        // TODO: refactor
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
        // TODO: refactor
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
}
