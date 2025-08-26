package dev.floelly.ghostnetfishing.integration.renderingAndFlow;

import dev.floelly.ghostnetfishing.testutil.AbstractH2Test;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Objects;

import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class NetsLayoutRenderingTest extends AbstractH2Test {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRenderRequestRecoveryButtonWithCorrectId_onNetsPage() throws Exception {
        //neues Netz eintragen
        double lat = getRandomLatitude();
        double lon = getRandomLongitude();
        String randomLatitude = formatDouble(lat);
        String randomLongitude = formatDouble(lon);
        mockMvc.perform(post(NETS_NEW_ENDPOINT)
                        .param(LOCATION_LAT, randomLatitude)
                        .param(LOCATION_LONG, randomLongitude)
                        .param(SIZE, "L")
                        .with(csrf()));

        MvcResult result = mockMvc.perform(get(NETS_ENDPOINT))
                .andReturn();
        Document document = Jsoup.parse(result.getResponse().getContentAsString());
        Elements tableRows = document.select("main tbody tr");
        assertThat(tableRows).isNotEmpty();
        assertThat(tableRows.size()).as("Should only display one net entry").isEqualTo(1);
        Element row = tableRows.first();
        assertNotNull(row);

        String id = row.attr("data-net-id");
        assertThat(id).isNotEmpty();

        Element form = row.selectFirst("form[method=post][action$=/request-recovery]");
        assertNotNull(form);
        assertThat(form.attr("action")).isEqualTo(String.format(REQUEST_NET_RECOVERY_ENDPOINT, Long.valueOf(id)));

        assertThat(Objects.requireNonNull(form).selectFirst("button")).isNotNull();
    }
}
