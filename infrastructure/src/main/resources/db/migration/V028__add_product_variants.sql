CREATE TABLE product_variants (
    id         BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    label      VARCHAR(100)   NOT NULL DEFAULT '',
    reference  VARCHAR(255)   NOT NULL,
    prix_ht    NUMERIC(10, 2) NOT NULL,
    stock      INT            NOT NULL DEFAULT 0,
    colisage   INT,
    position   INT            NOT NULL DEFAULT 0,
    version    INT            NOT NULL DEFAULT 0,
    CONSTRAINT chk_variant_stock_non_negative CHECK (stock >= 0)
);

CREATE INDEX idx_product_variants_product ON product_variants (product_id, position, id);

-- Chaque produit existant devient un produit à variante unique, au libellé vide.
INSERT INTO product_variants (product_id, label, reference, prix_ht, stock, colisage, position)
SELECT id, '', reference, prix_ht, stock, colisage, 0 FROM products;

-- order_items pointe désormais vers la variante (product_id conservé pour les jointures d'affichage).
ALTER TABLE order_items ADD COLUMN variant_id BIGINT REFERENCES product_variants(id);
UPDATE order_items oi SET variant_id = pv.id
  FROM product_variants pv WHERE pv.product_id = oi.product_id;
ALTER TABLE order_items ALTER COLUMN variant_id SET NOT NULL;
CREATE INDEX idx_order_items_variant ON order_items (variant_id);

-- reception_items idem : l'unité réceptionnée est la variante.
ALTER TABLE reception_items ADD COLUMN variant_id BIGINT REFERENCES product_variants(id);
UPDATE reception_items ri SET variant_id = pv.id
  FROM product_variants pv WHERE pv.product_id = ri.product_id;
ALTER TABLE reception_items ALTER COLUMN variant_id SET NOT NULL;
ALTER TABLE reception_items DROP CONSTRAINT reception_items_reception_id_product_id_key;
ALTER TABLE reception_items ADD CONSTRAINT uq_reception_items_variant
    UNIQUE (reception_id, variant_id);

-- L'index de recherche (V022) référence products.reference : à reconstruire sans lui.
DROP INDEX idx_products_search;
ALTER TABLE products DROP CONSTRAINT chk_stock_non_negative;
ALTER TABLE products
    DROP COLUMN reference,
    DROP COLUMN prix_ht,
    DROP COLUMN stock,
    DROP COLUMN colisage;
CREATE INDEX idx_products_search ON products USING gin (
    (LOWER(name) || ' ' || COALESCE(LOWER(description), '') || ' '
     || LOWER(brand) || ' ' || LOWER(supplier)) gin_trgm_ops
);
