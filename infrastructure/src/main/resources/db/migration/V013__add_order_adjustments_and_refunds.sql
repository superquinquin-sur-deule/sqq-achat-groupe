ALTER TABLE order_items ADD COLUMN cancelled_quantity INT NOT NULL DEFAULT 0;
ALTER TABLE order_items ADD CONSTRAINT chk_cancelled_quantity
    CHECK (cancelled_quantity >= 0 AND cancelled_quantity <= quantity);

CREATE TABLE refunds (
    id BIGSERIAL PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES orders(id),
    amount DECIMAL(10,2) NOT NULL,
    stripe_refund_id VARCHAR(255) UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
