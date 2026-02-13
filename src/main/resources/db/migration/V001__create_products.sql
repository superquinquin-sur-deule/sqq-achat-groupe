CREATE TABLE products (
    id         BIGSERIAL    PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    description TEXT,
    price      NUMERIC(10, 2) NOT NULL,
    supplier   VARCHAR(255) NOT NULL,
    stock      INT          NOT NULL DEFAULT 0,
    active     BOOLEAN      NOT NULL DEFAULT true
);

CREATE INDEX idx_products_active ON products (active);
