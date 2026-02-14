ALTER TABLE orders ADD COLUMN public_id UUID NOT NULL DEFAULT gen_random_uuid();
CREATE UNIQUE INDEX idx_orders_public_id ON orders (public_id);
