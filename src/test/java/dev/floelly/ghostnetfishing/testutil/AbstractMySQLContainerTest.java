package dev.floelly.ghostnetfishing.testutil;


import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@ActiveProfiles("mysql-container-test")
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public abstract class AbstractMySQLContainerTest {

    public static final MySQLContainer<?> MYSQL_CONTAINER;

    @Autowired
    protected DataSource dataSource;

    static {
        MYSQL_CONTAINER = new MySQLContainer<>("mysql:8.0")
                .withDatabaseName("ghostnet_test")
                .withUsername("spring-user")
                .withPassword("spring-password");
        MYSQL_CONTAINER.start();
    }

    @DynamicPropertySource
    static void datasourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
    }


    @BeforeEach
    void cleanDatabase() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            // hole alle Tabellen automatisch
            ResultSet rs = conn.getMetaData().getTables(null, null, "%", new String[]{"TABLE"});
            while(rs.next()){
                String table = rs.getString("TABLE_NAME");
                // behalte users und authorities
                if(table.equals("users") || table.equals("authorities")){
                    continue;
                }
                stmt.execute("TRUNCATE TABLE " + table);
            }
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }
}