CREATE TABLE order_status_histories (
    id          BIGSERIAL   PRIMARY KEY,
    order_id    BIGINT      NOT NULL REFERENCES orders(id),
    from_status VARCHAR(50),
    to_status   VARCHAR(50) NOT NULL,
    reason      VARCHAR(255),
    changed_at  TIMESTAMP   NOT NULL
);