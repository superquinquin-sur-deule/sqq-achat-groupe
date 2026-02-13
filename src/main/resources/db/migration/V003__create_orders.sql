CREATE TABLE orders (
    id              BIGSERIAL       PRIMARY KEY,
    order_number    VARCHAR(20)     UNIQUE NOT NULL,
    customer_name   VARCHAR(255)    NOT NULL,
    customer_email  VARCHAR(255)    NOT NULL,
    customer_phone  VARCHAR(20)     NOT NULL,
    time_slot_id    BIGINT          NOT NULL REFERENCES time_slots(id),
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    total_amount    NUMERIC(10, 2)  NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_orders_time_slot ON orders (time_slot_id);

CREATE TABLE order_items (
    id          BIGSERIAL       PRIMARY KEY,
    order_id    BIGINT          NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id  BIGINT          NOT NULL REFERENCES products(id),
    quantity    INT             NOT NULL CHECK (quantity > 0),
    unit_price  NUMERIC(10, 2) NOT NULL
);

CREATE INDEX idx_order_items_order ON order_items (order_id);
