-- Repeatable Flyway migration: dev seed data
-- Only loaded in dev profile via %dev.quarkus.flyway.locations

-- Clean existing seed data (idempotent)
DELETE FROM order_items WHERE order_id IN (SELECT id FROM orders WHERE customer_email = 'dev@test.fr');
DELETE FROM orders WHERE customer_email = 'dev@test.fr';
DELETE FROM products WHERE vente_id IN (SELECT id FROM ventes WHERE name LIKE '[DEV]%');
DELETE FROM time_slots WHERE vente_id IN (SELECT id FROM ventes WHERE name LIKE '[DEV]%');
DELETE FROM ventes WHERE name LIKE '[DEV]%';

-- Vente
INSERT INTO ventes (id, name, description, status)
VALUES (9000, '[DEV] Vente de printemps', 'Vente groupée de produits locaux - printemps 2026', 'ACTIVE')
ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, description = EXCLUDED.description, status = EXCLUDED.status;

-- Products
INSERT INTO products (id, name, description, price, supplier, stock, active, vente_id) VALUES
(9001, 'Pommes Gala',        'Pommes Gala Bio, 1kg',             3.50,  'Verger du Soleil',    50, true,  9000),
(9002, 'Carottes',           'Carottes de pleine terre, 1kg',    2.80,  'Ferme de la Vallée',  40, true,  9000),
(9003, 'Miel de fleurs',    'Pot de miel toutes fleurs, 500g',  8.90,  'Rucher des Collines', 25, true,  9000),
(9004, 'Oeufs fermiers',    'Boîte de 6 oeufs plein air',       3.20,  'Ferme de la Vallée',  60, true,  9000),
(9005, 'Pain de campagne',  'Pain au levain, 500g',              4.50,  'Boulangerie Martin',  30, true,  9000),
(9006, 'Fromage de chèvre', 'Bûche de chèvre affinée, 200g',   5.60,  'Chèvrerie du Lac',    20, true,  9000),
(9007, 'Jus de pomme',      'Jus de pomme artisanal, 1L',       4.20,  'Verger du Soleil',    35, true,  9000),
(9008, 'Confiture fraises', 'Confiture de fraises, 350g',        5.90,  'Conserverie Locale',  15, true,  9000),
(9009, 'Salade verte',      'Salade batavia, pièce',            1.80,  'Maraîcher Bio',       45, true,  9000),
(9010, 'Yaourt nature',     'Lot de 4 yaourts nature, 500g',    3.40,  'Laiterie du Pré',     40, true,  9000)
ON CONFLICT (id) DO UPDATE SET
  name = EXCLUDED.name, description = EXCLUDED.description, price = EXCLUDED.price,
  supplier = EXCLUDED.supplier, stock = EXCLUDED.stock, active = EXCLUDED.active, vente_id = EXCLUDED.vente_id;

-- Time slots (dates in the future)
INSERT INTO time_slots (id, date, start_time, end_time, capacity, reserved, vente_id) VALUES
(9001, CURRENT_DATE + INTERVAL '7 days',  '09:00', '10:00', 15, 0, 9000),
(9002, CURRENT_DATE + INTERVAL '7 days',  '10:00', '11:00', 15, 0, 9000),
(9003, CURRENT_DATE + INTERVAL '7 days',  '11:00', '12:00', 15, 0, 9000),
(9004, CURRENT_DATE + INTERVAL '14 days', '09:00', '10:00', 20, 0, 9000),
(9005, CURRENT_DATE + INTERVAL '14 days', '10:00', '11:00', 20, 0, 9000)
ON CONFLICT (id) DO UPDATE SET
  date = EXCLUDED.date, start_time = EXCLUDED.start_time, end_time = EXCLUDED.end_time,
  capacity = EXCLUDED.capacity, reserved = EXCLUDED.reserved, vente_id = EXCLUDED.vente_id;
