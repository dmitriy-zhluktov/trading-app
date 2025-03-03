package ru.arc.config.properties;


import lombok.Builder;
import lombok.Getter;

@Builder
public final class PostgresDbProperties {

  @Builder.Default
  public final String driver = "org.postgresql.Driver";
  public final String url;
  public final String schema;
  public final String user;
  public final String password;
  @Builder.Default
  public final int maxPoolSize = 16;
  @Builder.Default
  public final int minPoolSize = 8;
  @Builder.Default
  public final int idleTimeout = 60000;
}
