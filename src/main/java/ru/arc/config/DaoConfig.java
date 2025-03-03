package ru.arc.config;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.arc.dao.CandleDao;
import ru.arc.dao.SignalDao;
import ru.arc.dao.impl.CandleDaoImpl;
import ru.arc.dao.impl.SignalDaoImpl;

@Configuration
public class DaoConfig {

  @Bean
  public SignalDao signalDao(final DSLContext dsl) {
    return new SignalDaoImpl(dsl);
  }

  @Bean
  public CandleDao candleDao(final DSLContext dsl) {
    return new CandleDaoImpl(dsl);
  }
}
