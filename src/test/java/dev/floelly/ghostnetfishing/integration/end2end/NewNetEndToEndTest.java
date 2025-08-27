package dev.floelly.ghostnetfishing.integration.end2end;

import dev.floelly.ghostnetfishing.testutil.AbstractMySQLContainerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
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

        int responseStatus = mockMvc.perform(post(NETS_NEW_ENDPOINT)
                        .param(LOCATION_LAT, randomLatitude)
                        .param(LOCATION_LONG, randomLongitude)
                        .param(SIZE, size)
                        .with(csrf()))
                .andReturn()
                .getResponse()
                .getStatus();

        assertThat(responseStatus).isLessThan(400);

        try (Connection conn = DriverManager.getConnection(
                MYSQL_CONTAINER.getJdbcUrl(), MYSQL_CONTAINER.getUsername(), MYSQL_CONTAINER.getPassword());
            PreparedStatement ps = conn.prepareStatement(
                    String.format("SELECT %s, %s, %s FROM %s ORDER BY id DESC",
                            DB_COLUMN_LATITUDE,
                            DB_COLUMN_LONGITUDE,
                            DB_COLUMN_SIZE,
                            DB_COLUMN_NETS));
            ResultSet rs = ps.executeQuery()) {

            assertTrue(rs.next(), "Should persist at least one net in database");

            double dbLat = rs.getDouble(DB_COLUMN_LATITUDE);
            double dbLong = rs.getDouble(DB_COLUMN_LONGITUDE);
            String dbSize = rs.getString(DB_COLUMN_SIZE);

            assertEquals(Double.parseDouble(randomLatitude), dbLat, 0.0001, "Latitude of first persisted net does not match input value");
            assertEquals(Double.parseDouble(randomLongitude), dbLong, 0.0001, "Longitude of first persisted net does not match input value");
            assertEquals(size, dbSize, "Size of first persisted net does not match input value");
            assertFalse(rs.next(), "Should not have more than one persisted net");
        }

    }
}
