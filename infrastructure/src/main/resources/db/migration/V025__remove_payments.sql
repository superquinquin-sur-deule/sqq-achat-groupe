-- Le paiement se fait désormais en magasin : suppression de toute la partie paiement en ligne.

DROP TABLE IF EXISTS refunds;
DROP TABLE IF EXISTS payments;

ALTER TABLE products DROP COLUMN IF EXISTS stripe_product_id;
ALTER TABLE products DROP COLUMN IF EXISTS stripe_tax_code;

-- PENDING (en attente de paiement) et PAID fusionnent en CONFIRMED :
-- une commande est confirmée dès sa création, puis payée au retrait en magasin.
UPDATE orders SET status = 'CONFIRMED' WHERE status IN ('PENDING', 'PAID');
ALTER TABLE orders ALTER COLUMN status SET DEFAULT 'CONFIRMED';
