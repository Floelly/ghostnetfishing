package dev.floelly.ghostnetfishing.integration.end2end;

import dev.floelly.ghostnetfishing.testutil.AbstractMySQLContainerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.sql.*;

import static dev.floelly.ghostnetfishing.testutil.MvcTestFunctions.sendPostRequestAndExpectRedirect;
import static dev.floelly.ghostnetfishing.testutil.MvcTestFunctions.sendPostRequestAndExpectRedirectToNetsPage;
import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class RequestRecoveryEndToEndTest extends AbstractMySQLContainerTest {
    private static final String GET_NET_RECOVERY_FOR_REPORTED_NET_QUERY = String.format("Select %s, %s FROM %s Where %s = %s LIMIT 1", "state", "user_id", "nets", "net_id", REPORTED_NET_ID);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setupDatabase() throws Exception {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
                new ClassPathResource("sql/populate-default-user.sql"),
                new ClassPathResource("sql/populate-nets-table-diverse.sql")
        );
        populator.execute(dataSource);
    }

    @Test
    @WithMockUser(username = "userwithnumber", roles = {SPRING_SECURITY_STANDARD_ROLE, SPRING_SECURITY_RECOVERER_ROLE})
    void shouldLinkUserToNet_whenPhoneNumber_onRequestRecovery() throws Exception {
        sendPostRequestAndExpectRedirectToNetsPage(mockMvc, String.format(REQUEST_NET_RECOVERY_ENDPOINT, Long.valueOf(REPORTED_NET_ID)));

        try (Connection connection = getTestContainerConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_NET_RECOVERY_FOR_REPORTED_NET_QUERY);
             ResultSet rs = preparedStatement.executeQuery();
        )
        {
            assertTrue(rs.next());
            assertThat(rs.getObject("user_id", Long.class)).isEqualTo(Long.valueOf(2));
            assertThat(rs.getString("state")).isEqualTo(RECOVERY_PENDING);
        }
    }

    @Test
    @WithMockUser(username = "user", roles = {SPRING_SECURITY_STANDARD_ROLE})
    void shouldNotLinkUserToNet_whenNoPhoneNumber_onRequestRecovery() throws Exception {
        sendPostRequestAndExpectRedirectToNetsPage(mockMvc, String.format(REQUEST_NET_RECOVERY_ENDPOINT, Long.valueOf(REPORTED_NET_ID)));

        try (Connection connection = getTestContainerConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_NET_RECOVERY_FOR_REPORTED_NET_QUERY);
             ResultSet rs = preparedStatement.executeQuery();
        )
        {
            assertTrue(rs.next());
            assertThat(rs.getObject("user_id", Long.class)).isNull();
            assertThat(rs.getString("state")).isEqualTo(REPORTED);
        }
    }

    @Test
    void shouldNotLinkUserToNet_whenUserNotLoggedIn_onRequestRecovery() throws Exception {
        sendPostRequestAndExpectRedirect(mockMvc, String.format(REQUEST_NET_RECOVERY_ENDPOINT, Long.valueOf(REPORTED_NET_ID)));

        try (Connection connection = getTestContainerConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_NET_RECOVERY_FOR_REPORTED_NET_QUERY);
             ResultSet rs = preparedStatement.executeQuery();
        )
        {
            assertTrue(rs.next());
            assertThat(rs.getObject("user_id", Long.class)).isNull();
            assertThat(rs.getString("state")).isEqualTo(REPORTED);
        }
    }
}
