CREATE TABLE receptions (
    id BIGSERIAL PRIMARY KEY,
    vente_id BIGINT NOT NULL REFERENCES ventes(id),
    supplier VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (vente_id, supplier)
);

CREATE TABLE reception_items (
    id BIGSERIAL PRIMARY KEY,
    reception_id BIGINT NOT NULL REFERENCES receptions(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id),
    ordered_quantity INT NOT NULL CHECK (ordered_quantity >= 0),
    received_quantity INT NOT NULL CHECK (received_quantity >= 0),
    UNIQUE (reception_id, product_id)
);
