package ru.arc.config;

import com.zaxxer.hikari.HikariConfig;
import org.jooq.DSLContext;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import ru.arc.config.properties.PostgresDbProperties;
import ru.arc.dao.SignalDao;
import ru.arc.dao.impl.SignalDaoImpl;

import javax.sql.DataSource;

import static org.jooq.SQLDialect.POSTGRES;


public class DatabaseTestConfig {

  PostgresDbProperties postgresDbProperties() {
    return PostgresDbProperties.builder()
            .url("jdbc:postgresql://localhost:5432/postgres")
            .schema("trading_app")
            .user("postgres")
            .password("postgres")
            .build();
  }

  DataSource dataSource() {
    final PostgresDbProperties databaseProperties = postgresDbProperties();
    final HikariConfig config = new HikariConfig();
    config.setAutoCommit(true);
    config.setDriverClassName(databaseProperties.driver);
    config.setJdbcUrl(databaseProperties.url + "?currentSchema=" + databaseProperties.schema);
    config.setUsername(databaseProperties.user);
    config.setPassword(databaseProperties.password);
    config.setMaximumPoolSize(databaseProperties.maxPoolSize);
    config.setMinimumIdle(databaseProperties.minPoolSize);
    config.setIdleTimeout(databaseProperties.idleTimeout);
    return new com.zaxxer.hikari.HikariDataSource(config);
  }

  public DSLContext dslContext() {
    final DefaultConfiguration config = new DefaultConfiguration();
    config.set(new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource())));
    config.setSQLDialect(POSTGRES);

    return new DefaultDSLContext(config);
  }
}
