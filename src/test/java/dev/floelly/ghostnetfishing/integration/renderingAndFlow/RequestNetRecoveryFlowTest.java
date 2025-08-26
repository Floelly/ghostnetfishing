package dev.floelly.ghostnetfishing.integration.renderingAndFlow;

import dev.floelly.ghostnetfishing.testutil.AbstractH2Test;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RequestNetRecoveryFlowTest extends AbstractH2Test {
    private static final Long NET_ID = 0L;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "standard-user", roles = {STANDARD_ROLE})
    void shouldChangeStateInFrontEnd_whenSuccessful_onRequestNetRecovery() throws Exception {
        //setup net
        double lat = getRandomLatitude();
        double lon = getRandomLongitude();
        String randomLatitude = formatDouble(lat);
        String randomLongitude = formatDouble(lon);
        MvcResult createResult = mockMvc.perform(post(NETS_NEW_ENDPOINT)
                        .param(LOCATION_LAT, randomLatitude)
                        .param(LOCATION_LONG, randomLongitude)
                        .param(SIZE, "L")
                        .with(csrf()))
                .andReturn();
        String redirectedUrl = createResult.getResponse().getRedirectedUrl();
        assertNotNull(redirectedUrl, "redirectedUrl of reporting a new net should not be null");

        //get net id
        MvcResult redirectResult = mockMvc.perform(get(redirectedUrl))
                .andExpect(status().isOk())
                .andReturn();
        Document doc = Jsoup.parse(redirectResult.getResponse().getContentAsString());
        Elements rows = doc.select("main tr");
        Element matchingRow = rows.stream()
                .filter(row -> row.select("td").text().contains(randomLatitude)
                        && row.select("td").text().contains(randomLongitude))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No Net found for lat/lon"));

        String netId = matchingRow.attr("data-net-id");
        assertThat(netId).as("No net Id attribute found on row with matching lon/lat").isNotEmpty();

        //request recovery for net
        MvcResult requestRecoveryResult = mockMvc.perform(post(String.format(REQUEST_NET_RECOVERY_ENDPOINT, Long.valueOf(netId)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(NETS_ENDPOINT))
                .andReturn();
        String redirectedUrlOnRequestRecovery = requestRecoveryResult.getResponse().getRedirectedUrl();
        assertNotNull(redirectedUrlOnRequestRecovery);

        //check recovered status on net
        MvcResult finalResult = mockMvc.perform(get(redirectedUrlOnRequestRecovery))
                .andExpect(status().isOk())
                .andReturn();
        Document finalDoc = Jsoup.parse(finalResult.getResponse().getContentAsString());
        Elements finalRows = finalDoc.select("main tr");
        Element netRow = finalRows.stream()
                .filter(row -> row.attr("data-net-id").equals(netId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No Net found for net Id: " + netId));
        String rowEntries = netRow.select("td").text();
        assertThat(rowEntries).as("Net status should be RECOVERY_PENDING").contains("RECOVERY_PENDING");
    }
}
