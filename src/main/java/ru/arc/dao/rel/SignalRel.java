package ru.arc.dao.rel;

import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.jooq.impl.DSL.name;

public final class SignalRel extends TableImpl<Record> {

  public static SignalRel INSTANCE = new SignalRel();

  public static final TableField<Record, String> SYMBOL = createField(name("symbol"),
      SQLDataType.VARCHAR, INSTANCE);

  public static final TableField<Record, String> DIRECTION = createField(name("direction"),
      SQLDataType.VARCHAR, INSTANCE);

  public static final TableField<Record, OffsetDateTime> TRIGGER_DATE =
      createField(name("trigger_date"), SQLDataType.OFFSETDATETIME, INSTANCE);

  public static final TableField<Record, BigDecimal> TREND_UPPER_BOUND = createField(name("trend_upper_bound"),
      SQLDataType.NUMERIC, INSTANCE);

  public static final TableField<Record, BigDecimal> TREND_LOWER_BOUND = createField(name("trend_lower_bound"),
          SQLDataType.NUMERIC, INSTANCE);

  public static final TableField<Record, BigDecimal> LAST_CLOSE = createField(name("last_close"),
          SQLDataType.NUMERIC, INSTANCE);


  private SignalRel() {
    super(name("signal"));
  }
}
