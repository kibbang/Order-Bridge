CREATE TABLE orders (
    id               BIGSERIAL    PRIMARY KEY,
    channel_order_no VARCHAR(100) NOT NULL UNIQUE,
    channel_id       BIGINT       NOT NULL REFERENCES channels(id),
    orderer_name     VARCHAR(100) NOT NULL,
    orderer_phone    VARCHAR(20)  NOT NULL,
    receiver_name    VARCHAR(100) NOT NULL,
    receiver_phone   VARCHAR(20)  NOT NULL,
    delivery_address VARCHAR(255) NOT NULL,
    delivery_memo    VARCHAR(255),
    total_amount     INTEGER      NOT NULL,
    status           VARCHAR(50)  NOT NULL,
    ordered_at       TIMESTAMP    NOT NULL,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);