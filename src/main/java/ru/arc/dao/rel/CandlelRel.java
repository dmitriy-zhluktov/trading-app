package ru.arc.dao.rel;

import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.jooq.impl.DSL.name;

public final class CandlelRel extends TableImpl<Record> {

  public static CandlelRel INSTANCE = new CandlelRel();

  public static final TableField<Record, String> SYMBOL = createField(name("symbol"),
      SQLDataType.VARCHAR, INSTANCE);

  public static final TableField<Record, Long> DATETIME =
      createField(name("datetime"), SQLDataType.BIGINT, INSTANCE);

  public static final TableField<Record, BigDecimal> HIGH_PRICE = createField(name("high_price"),
      SQLDataType.NUMERIC, INSTANCE);

  public static final TableField<Record, BigDecimal> LOW_PRICE = createField(name("low_price"),
          SQLDataType.NUMERIC, INSTANCE);


  private CandlelRel() {
    super(name("candle"));
  }
}
