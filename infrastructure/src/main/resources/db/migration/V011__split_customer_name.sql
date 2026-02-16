ALTER TABLE orders ADD COLUMN customer_first_name VARCHAR(255);
ALTER TABLE orders ADD COLUMN customer_last_name VARCHAR(255);

-- Migrate existing data: last word = last name, rest = first name
UPDATE orders SET
  customer_last_name = SUBSTRING(customer_name FROM '([^\s]+)$'),
  customer_first_name = TRIM(SUBSTRING(customer_name FROM '^(.+)\s'));

-- Case where there's only a single word (no space)
UPDATE orders SET
  customer_first_name = customer_name,
  customer_last_name = ''
WHERE customer_first_name IS NULL;

ALTER TABLE orders ALTER COLUMN customer_first_name SET NOT NULL;
ALTER TABLE orders ALTER COLUMN customer_last_name SET NOT NULL;
ALTER TABLE orders DROP COLUMN customer_name;

-- Recreate search index for the new columns (replaces idx_orders_search from V010)
DROP INDEX IF EXISTS idx_orders_search;
CREATE INDEX idx_orders_search ON orders (vente_id, status, lower(customer_last_name) text_pattern_ops, lower(customer_first_name) text_pattern_ops);
