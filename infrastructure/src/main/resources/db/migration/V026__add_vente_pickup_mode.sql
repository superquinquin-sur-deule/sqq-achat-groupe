ALTER TABLE ventes ADD COLUMN pickup_mode VARCHAR(20) NOT NULL DEFAULT 'TIME_SLOTS';
ALTER TABLE ventes ADD COLUMN pickup_start_date DATE;
ALTER TABLE ventes ADD COLUMN pickup_end_date DATE;

ALTER TABLE ventes ADD CONSTRAINT chk_vente_pickup_range CHECK (
    (pickup_mode = 'TIME_SLOTS' AND pickup_start_date IS NULL AND pickup_end_date IS NULL)
 OR (pickup_mode = 'DATE_RANGE' AND pickup_start_date IS NOT NULL
     AND pickup_end_date IS NOT NULL AND pickup_end_date >= pickup_start_date)
);
