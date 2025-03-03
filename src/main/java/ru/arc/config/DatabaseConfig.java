package ru.arc.config;

import com.zaxxer.hikari.HikariConfig;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.jooq.DSLContext;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.arc.config.properties.PostgresDbProperties;

import javax.sql.DataSource;

import static org.jooq.SQLDialect.POSTGRES;


@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

  @Bean
  PostgresDbProperties postgresDbProperties(
          @Value("${jdbc.url}") final String url,
          @Value("${jdbc.schema}") final String schema,
          @Value("${jdbc.user}") final String user,
          @Value("${jdbc.password}") final String password
  ) {
    return PostgresDbProperties.builder()
            .url(url)
            .schema(schema)
            .user(user)
            .password(password)
            .build();
  }

  @Bean
  DataSource dataSource(final PostgresDbProperties databaseProperties) {
    final HikariConfig config = new HikariConfig();
    config.setAutoCommit(false);
    config.setDriverClassName(databaseProperties.driver);
    config.setJdbcUrl(databaseProperties.url + "?currentSchema=" + databaseProperties.schema);
    config.setUsername(databaseProperties.user);
    config.setPassword(databaseProperties.password);
    config.setMaximumPoolSize(databaseProperties.maxPoolSize);
    config.setMinimumIdle(databaseProperties.minPoolSize);
    config.setIdleTimeout(databaseProperties.idleTimeout);
    config.setRegisterMbeans(true);
    return new com.zaxxer.hikari.HikariDataSource(config);
  }

  @Bean
  PlatformTransactionManager transactionManager(final DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  @Bean(initMethod = "migrate")
  Flyway flyway(final DataSource dataSource,
                final PostgresDbProperties databaseProperties) {
    final ClassicConfiguration configuration = new ClassicConfiguration();
    configuration.setDataSource(dataSource);
    configuration.setShouldCreateSchemas(true);
    configuration.setSchemas(new String[] {databaseProperties.schema});
    return new Flyway(configuration);
  }

  @Bean
  @DependsOn("flyway")
  DSLContext dslContext(final DataSource dataSource) {
    final DefaultConfiguration config = new DefaultConfiguration();
    config.set(new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource)));
    config.setSQLDialect(POSTGRES);

    return new DefaultDSLContext(config);
  }
}
