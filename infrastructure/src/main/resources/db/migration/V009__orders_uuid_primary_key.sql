-- Drop FK constraints referencing orders(id)
ALTER TABLE order_items DROP CONSTRAINT order_items_order_id_fkey;
ALTER TABLE payments DROP CONSTRAINT payments_order_id_fkey;
DROP INDEX idx_order_items_order;
DROP INDEX idx_payments_order;

-- Add UUID columns, populate from orders.public_id
ALTER TABLE order_items ADD COLUMN order_uuid UUID;
UPDATE order_items oi SET order_uuid = o.public_id FROM orders o WHERE oi.order_id = o.id;
ALTER TABLE order_items ALTER COLUMN order_uuid SET NOT NULL;
ALTER TABLE order_items DROP COLUMN order_id;
ALTER TABLE order_items RENAME COLUMN order_uuid TO order_id;

ALTER TABLE payments ADD COLUMN order_uuid UUID;
UPDATE payments p SET order_uuid = o.public_id FROM orders o WHERE p.order_id = o.id;
ALTER TABLE payments ALTER COLUMN order_uuid SET NOT NULL;
ALTER TABLE payments DROP COLUMN order_id;
ALTER TABLE payments RENAME COLUMN order_uuid TO order_id;

-- Switch orders PK from BIGSERIAL to UUID
ALTER TABLE orders DROP CONSTRAINT orders_pkey;
DROP INDEX IF EXISTS idx_orders_public_id;
ALTER TABLE orders DROP COLUMN id;
ALTER TABLE orders RENAME COLUMN public_id TO id;
ALTER TABLE orders ADD PRIMARY KEY (id);

-- Re-add FK constraints and indexes
ALTER TABLE order_items ADD CONSTRAINT order_items_order_id_fkey
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE;
ALTER TABLE payments ADD CONSTRAINT payments_order_id_fkey
    FOREIGN KEY (order_id) REFERENCES orders(id);
ALTER TABLE payments ADD CONSTRAINT payments_order_id_key UNIQUE (order_id);
CREATE INDEX idx_order_items_order ON order_items (order_id);
CREATE INDEX idx_payments_order ON payments (order_id);
