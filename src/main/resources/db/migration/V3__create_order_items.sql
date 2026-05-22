CREATE TABLE order_items (
    id           BIGSERIAL    PRIMARY KEY,
    order_id     BIGINT       NOT NULL REFERENCES orders(id),
    product_name VARCHAR(255) NOT NULL,
    product_code VARCHAR(100) NOT NULL,
    seller_code  VARCHAR(100) NOT NULL,
    item_seq     INTEGER      NOT NULL,
    unit_price   INTEGER      NOT NULL,
    item_status  VARCHAR(50)  NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);