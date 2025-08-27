package dev.floelly.ghostnetfishing.testutil;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;

public class MvcTestFunctions {
    public static MvcResult sendPostRequestAndExpectRedirectToNetsPage(MockMvc mockMvc, String url) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(NETS_ENDPOINT))
                .andReturn();
    }

    public static Document sendGetRequestToNetsPage(MockMvc mockMvc) throws Exception {
        return sendGetRequestToNetsPage(mockMvc, null);
    }

    public static Document sendGetRequestToNetsPage(MockMvc mockMvc, @Nullable MockHttpSession session) throws Exception {
        MvcResult result = performSuccessfulGet(mockMvc, NETS_ENDPOINT, session);
        return Jsoup.parse(result.getResponse().getContentAsString());
    }

    public static MvcResult performSuccessfulGet(MockMvc mockMvc, String url, @Nullable MockHttpSession session) throws Exception {
        var builder = Objects.isNull(session) ? MockMvcRequestBuilders.get(url) : MockMvcRequestBuilders.get(url).session(session);
        return mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    public static @NotNull MockHttpSession getSession(MvcResult requestRecoveryResult) {
        MockHttpSession session = (MockHttpSession) requestRecoveryResult.getRequest().getSession(false);
        Assertions.assertNotNull(session);
        return session;
    }

    public static @NotNull MvcResult assertPostNewNetSuccessful(MockMvc mockMvc, String randomLatitude, String randomLongitude, String randomSize) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(NETS_NEW_ENDPOINT)
                        .param(LOCATION_LAT, randomLatitude)
                        .param(LOCATION_LONG, randomLongitude)
                        .param(SIZE, randomSize)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(NETS_ENDPOINT))
                .andReturn();
    }
}