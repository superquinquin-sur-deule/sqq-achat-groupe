ALTER TABLE products ADD COLUMN prix_ht NUMERIC(10, 2);
ALTER TABLE products ADD COLUMN taux_tva NUMERIC(5, 2);
UPDATE products SET prix_ht = price, taux_tva = 0;
ALTER TABLE products ALTER COLUMN prix_ht SET NOT NULL;
ALTER TABLE products ALTER COLUMN taux_tva SET NOT NULL;
ALTER TABLE products DROP COLUMN price;
