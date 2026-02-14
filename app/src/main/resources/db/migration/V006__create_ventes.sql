CREATE TABLE ventes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE products ADD COLUMN vente_id BIGINT NOT NULL;
ALTER TABLE products ADD CONSTRAINT fk_products_vente FOREIGN KEY (vente_id) REFERENCES ventes(id);
CREATE INDEX idx_products_vente ON products (vente_id);

ALTER TABLE time_slots ADD COLUMN vente_id BIGINT NOT NULL;
ALTER TABLE time_slots ADD CONSTRAINT fk_time_slots_vente FOREIGN KEY (vente_id) REFERENCES ventes(id);
CREATE INDEX idx_time_slots_vente ON time_slots (vente_id);

ALTER TABLE orders ADD COLUMN vente_id BIGINT NOT NULL;
ALTER TABLE orders ADD CONSTRAINT fk_orders_vente FOREIGN KEY (vente_id) REFERENCES ventes(id);
CREATE INDEX idx_orders_vente ON orders (vente_id);
