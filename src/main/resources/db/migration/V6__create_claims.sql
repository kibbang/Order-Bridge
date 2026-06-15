CREATE TABLE claims (
    id            BIGSERIAL    PRIMARY KEY,
    order_item_id BIGINT       NOT NULL REFERENCES order_items(id),
    claim_type    VARCHAR(50)  NOT NULL,
    status        VARCHAR(50)  NOT NULL,
    reason        VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE cancels (
    id            BIGINT      PRIMARY KEY REFERENCES claims(id),
    refund_amount INTEGER     NOT NULL,
    refund_method VARCHAR(50) NOT NULL
);

CREATE TABLE returns (
    id             BIGINT       PRIMARY KEY REFERENCES claims(id),
    pickup_address VARCHAR(255) NOT NULL,
    carrier_code   VARCHAR(50)  NOT NULL,
    tracking_no    VARCHAR(100),
    refund_amount  INTEGER      NOT NULL,
    refund_method  VARCHAR(50)  NOT NULL
);

CREATE TABLE exchanges (
    id                    BIGINT       PRIMARY KEY REFERENCES claims(id),
    exchange_product_code VARCHAR(100) NOT NULL,
    delivery_address      VARCHAR(255) NOT NULL,
    carrier_code          VARCHAR(50)  NOT NULL,
    tracking_no           VARCHAR(100)
);