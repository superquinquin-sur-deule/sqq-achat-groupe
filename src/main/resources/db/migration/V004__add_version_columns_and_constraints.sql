-- Add optimistic locking version columns
ALTER TABLE products ADD COLUMN version INT NOT NULL DEFAULT 0;
ALTER TABLE time_slots ADD COLUMN version INT NOT NULL DEFAULT 0;

-- Add CHECK constraint to prevent overbooking
ALTER TABLE time_slots ADD CONSTRAINT chk_reserved_le_capacity CHECK (reserved <= capacity);
