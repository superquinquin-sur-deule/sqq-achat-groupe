-- Numéro de coopérateur saisi par le client au moment de la commande.
-- Les commandes antérieures n'en ont pas : on les rétro-remplit avec une chaîne vide.
ALTER TABLE orders ADD COLUMN cooperator_number VARCHAR(50);

UPDATE orders SET cooperator_number = '' WHERE cooperator_number IS NULL;

ALTER TABLE orders ALTER COLUMN cooperator_number SET NOT NULL;
