CREATE TABLE signal (
    symbol                    VARCHAR      NOT NULL,
    direction                 VARCHAR      NOT NULL,
    trigger_date              TIMESTAMP    NOT NULL,
    trend_upper_bound         NUMERIC(19, 10) NOT NULL,
    trend_lower_bound         NUMERIC(19, 10) NOT NULL,
    last_close                NUMERIC(19, 10) NOT NULL,
    CONSTRAINT signal_pk PRIMARY KEY (symbol, trigger_date)
);