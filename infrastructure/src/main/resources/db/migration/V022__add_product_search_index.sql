CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX idx_products_search ON products USING gin (
    (LOWER(name) || ' ' || COALESCE(LOWER(description), '') || ' ' || LOWER(brand) || ' ' || LOWER(supplier) || ' ' || LOWER(reference)) gin_trgm_ops
);
