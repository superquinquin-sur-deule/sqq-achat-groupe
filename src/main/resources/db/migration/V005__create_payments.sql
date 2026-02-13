CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE REFERENCES orders(id),
    stripe_payment_id VARCHAR(255) UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    amount DECIMAL(10,2) NOT NULL,
    attempts INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version INT NOT NULL DEFAULT 0
);

CREATE INDEX idx_payments_order ON payments (order_id);
CREATE INDEX idx_payments_stripe_id ON payments (stripe_payment_id);
