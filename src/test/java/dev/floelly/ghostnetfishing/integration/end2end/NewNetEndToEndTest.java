package dev.floelly.ghostnetfishing.integration.end2end;

import dev.floelly.ghostnetfishing.testutil.AbstractMySQLContainerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NewNetEndToEndTest extends AbstractMySQLContainerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loadsContext() {

    }

    @Test
    //TODO: Refactor!
    void persistsNewNet_onPostNewNet() throws Exception {
        String randomLatitude = getRandomLatitude();
        String randomLongitude = getRandomLongitude();
        String size = getRandomNetSize();

        mockMvc.perform(post(NETS_NEW_ENDPOINT)
                        .param(LOCATION_LAT, randomLatitude)
                        .param(LOCATION_LONG, randomLongitude)
                        .param(SIZE, size)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        try (Connection conn = getTestContainerConnection();
             PreparedStatement ps = conn.prepareStatement(
                    String.format("SELECT %s, %s, %s FROM %s ORDER BY id DESC",
                            DB_COLUMN_LATITUDE, DB_COLUMN_LONGITUDE, DB_COLUMN_SIZE, DB_TABLE_NETS));
             ResultSet rs = ps.executeQuery()

        ) {
            boolean exists = false;

            while (rs.next()) {
                double dbLat = rs.getDouble(DB_COLUMN_LATITUDE);
                double dbLong = rs.getDouble(DB_COLUMN_LONGITUDE);
                String dbSize = rs.getString(DB_COLUMN_SIZE);

                if (isEqualDouble(dbLat, randomLatitude) &&
                        isEqualDouble(dbLong, randomLongitude) &&
                        dbSize.equals(size)) {
                    exists = true;
                    break;
                }
            }
            assertTrue(exists, "Persisted net not found in database");
        }
    }

    private static boolean isEqualDouble(double dbLat, String randomLatitude) {
        return Math.abs(dbLat - Double.parseDouble(randomLatitude)) > 0.001;
    }
}
