ALTER TABLE orders ADD COLUMN version INT NOT NULL DEFAULT 0;
ALTER TABLE products ADD CONSTRAINT chk_stock_non_negative CHECK (stock >= 0);
