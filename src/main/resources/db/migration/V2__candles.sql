CREATE TABLE candle (
    symbol                    VARCHAR      NOT NULL,
    datetime                  BIGINT       NOT NULL,
    high_price                NUMERIC(19, 10) NOT NULL,
    low_price                 NUMERIC(19, 10) NOT NULL,
    CONSTRAINT candle_pk PRIMARY KEY (symbol, datetime)
);