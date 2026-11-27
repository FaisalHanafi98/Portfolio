package com.faisal.portfolio;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class FlywayBaselineSchemaTest {

    @Test
    void prodConfigLocksHibernateToValidateAndSeparatesFlywayCredentials() {
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(
                new ClassPathResource("application.yml"),
                new ClassPathResource("application-prod.yml")
        );
        Properties prodProps = yaml.getObject();
        assertThat(prodProps).isNotNull();

        assertThat(prodProps.getProperty("spring.jpa.hibernate.ddl-auto")).isEqualTo("validate");
        assertThat(prodProps.getProperty("spring.flyway.enabled")).isEqualTo("true");
        assertThat(prodProps.getProperty("spring.flyway.baseline-version")).isEqualTo("1");

        String flywayRawContent;
        try (var stream = new ClassPathResource("application-prod.yml").getInputStream()) {
            flywayRawContent = new String(stream.readAllBytes());
        } catch (Exception ex) {
            throw new AssertionError("Unable to read application-prod.yml", ex);
        }

        assertThat(flywayRawContent).contains("FLYWAY_USER");
        assertThat(flywayRawContent).contains("FLYWAY_PASSWORD");
    }

    @Test
    void flywayV1AppliesCleanlyToEmptyDbAndBaselinesExistingSchema() throws Exception {
        assertThat(new ClassPathResource("db/migration/V1__baseline_portfolio_schema.sql").exists())
                .as("Explicit V1 baseline SQL migration must exist on classpath")
                .isTrue();

        String emptyDbUrl = "jdbc:h2:mem:flyway_empty_test;DB_CLOSE_DELAY=-1";
        Flyway freshFlyway = Flyway.configure()
                .dataSource(emptyDbUrl, "sa", "")
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion("1")
                .load();

        var freshResult = freshFlyway.migrate();
        assertThat(freshResult.migrationsExecuted).isGreaterThanOrEqualTo(1);

        String preExistingDbUrl = "jdbc:h2:mem:flyway_preexisting_test;DB_CLOSE_DELAY=-1";
        DriverManagerDataSource ds = new DriverManagerDataSource(preExistingDbUrl, "sa", "");
        try (Connection conn = ds.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE projects (id BIGINT PRIMARY KEY, slug VARCHAR(100) NOT NULL)");
        }

        Flyway baselineFlyway = Flyway.configure()
                .dataSource(preExistingDbUrl, "sa", "")
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion("1")
                .load();

        baselineFlyway.migrate();
        var applied = baselineFlyway.info().applied();
        assertThat(applied).isNotEmpty();
        assertThat(applied[0].getVersion().getVersion()).isEqualTo("1");
        assertThat(baselineFlyway.info().current().getVersion().getVersion()).isEqualTo("2");
    }
}
