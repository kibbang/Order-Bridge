CREATE TABLE wms_deliveries (
    id            BIGSERIAL    PRIMARY KEY,
    order_id      BIGINT       NOT NULL UNIQUE REFERENCES orders(id),
    wms_order_no  VARCHAR(100),
    wms_url       VARCHAR(255),
    attempt_count INTEGER      NOT NULL DEFAULT 0,
    status        VARCHAR(50)  NOT NULL,
    last_error    TEXT,
    sent_at       TIMESTAMP,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);