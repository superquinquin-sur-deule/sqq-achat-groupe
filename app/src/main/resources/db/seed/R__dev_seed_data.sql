-- Repeatable Flyway migration: dev seed data
-- Only loaded in dev profile via %dev.quarkus.flyway.locations

-- Clean existing seed data (idempotent)
DELETE FROM order_items WHERE order_id IN (SELECT id FROM orders WHERE vente_id IN (SELECT id FROM ventes WHERE name LIKE '[DEV]%'));
DELETE FROM payments WHERE order_id IN (SELECT id FROM orders WHERE vente_id IN (SELECT id FROM ventes WHERE name LIKE '[DEV]%'));
DELETE FROM orders WHERE vente_id IN (SELECT id FROM ventes WHERE name LIKE '[DEV]%');
DELETE FROM products WHERE vente_id IN (SELECT id FROM ventes WHERE name LIKE '[DEV]%');
DELETE FROM time_slots WHERE vente_id IN (SELECT id FROM ventes WHERE name LIKE '[DEV]%');
DELETE FROM ventes WHERE name LIKE '[DEV]%';

-- Vente
INSERT INTO ventes (id, name, description, status, start_date, end_date)
VALUES (9000, '[DEV] Vente de printemps', 'Vente groupée de produits locaux - printemps 2026', 'ACTIVE',
        NOW() - INTERVAL '1 day', NOW() + INTERVAL '60 days')
ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, description = EXCLUDED.description, status = EXCLUDED.status,
    start_date = EXCLUDED.start_date, end_date = EXCLUDED.end_date;

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
(9001, CURRENT_DATE + INTERVAL '7 days',  '09:00', '10:00', 15, 2, 9000),
(9002, CURRENT_DATE + INTERVAL '7 days',  '10:00', '11:00', 15, 2, 9000),
(9003, CURRENT_DATE + INTERVAL '7 days',  '11:00', '12:00', 15, 1, 9000),
(9004, CURRENT_DATE + INTERVAL '14 days', '09:00', '10:00', 20, 1, 9000),
(9005, CURRENT_DATE + INTERVAL '14 days', '10:00', '11:00', 20, 0, 9000)
ON CONFLICT (id) DO UPDATE SET
  date = EXCLUDED.date, start_time = EXCLUDED.start_time, end_time = EXCLUDED.end_time,
  capacity = EXCLUDED.capacity, reserved = EXCLUDED.reserved, vente_id = EXCLUDED.vente_id;

-- Orders (PAID)
INSERT INTO orders (id, order_number, customer_name, customer_email, customer_phone, time_slot_id, status, total_amount, vente_id, created_at) VALUES
(9001, 'ORD-9001', 'Marie Dupont',    'marie.dupont@email.fr',    '06 12 34 56 78', 9001, 'PAID', 20.90, 9000, NOW() - INTERVAL '2 days'),
(9002, 'ORD-9002', 'Jean Martin',     'jean.martin@email.fr',     '06 23 45 67 89', 9001, 'PAID', 16.60, 9000, NOW() - INTERVAL '2 days'),
(9003, 'ORD-9003', 'Sophie Bernard',  'sophie.bernard@email.fr',  '06 34 56 78 90', 9002, 'PAID', 25.10, 9000, NOW() - INTERVAL '1 day'),
(9004, 'ORD-9004', 'Pierre Leroy',    'pierre.leroy@email.fr',    '06 45 67 89 01', 9002, 'PAID', 12.30, 9000, NOW() - INTERVAL '1 day'),
(9005, 'ORD-9005', 'Claire Moreau',   'claire.moreau@email.fr',   '06 56 78 90 12', 9003, 'PAID', 18.40, 9000, NOW() - INTERVAL '12 hours'),
(9006, 'ORD-9006', 'Lucas Petit',     'lucas.petit@email.fr',     '06 67 89 01 23', 9004, 'PAID', 31.70, 9000, NOW() - INTERVAL '6 hours')
ON CONFLICT (id) DO UPDATE SET
  order_number = EXCLUDED.order_number, customer_name = EXCLUDED.customer_name,
  customer_email = EXCLUDED.customer_email, customer_phone = EXCLUDED.customer_phone,
  time_slot_id = EXCLUDED.time_slot_id, status = EXCLUDED.status,
  total_amount = EXCLUDED.total_amount, vente_id = EXCLUDED.vente_id;

-- Order items
INSERT INTO order_items (id, order_id, product_id, quantity, unit_price) VALUES
-- Marie Dupont: Pommes Gala x2, Miel x1, Carottes x1
(9001, 9001, 9001, 2, 3.50),
(9002, 9001, 9003, 1, 8.90),
(9003, 9001, 9002, 1, 2.80),
-- Jean Martin: Oeufs x2, Pain x1, Salade x2
(9004, 9002, 9004, 2, 3.20),
(9005, 9002, 9005, 1, 4.50),
(9006, 9002, 9009, 2, 1.80),
-- Sophie Bernard: Fromage x2, Jus de pomme x1, Confiture x1, Carottes x1
(9007, 9003, 9006, 2, 5.60),
(9008, 9003, 9007, 1, 4.20),
(9009, 9003, 9008, 1, 5.90),
(9010, 9003, 9002, 1, 2.80),
-- Pierre Leroy: Pommes Gala x1, Yaourt x1, Salade x2
(9011, 9004, 9001, 1, 3.50),
(9012, 9004, 9010, 1, 3.40),
(9013, 9004, 9009, 2, 1.80),
-- Claire Moreau: Miel x1, Pain x1, Oeufs x1
(9014, 9005, 9003, 1, 8.90),
(9015, 9005, 9005, 1, 4.50),
(9016, 9005, 9004, 1, 3.20),
-- Lucas Petit: Fromage x1, Confiture x2, Jus de pomme x2, Pommes Gala x1
(9017, 9006, 9006, 1, 5.60),
(9018, 9006, 9008, 2, 5.90),
(9019, 9006, 9007, 2, 4.20),
(9020, 9006, 9001, 1, 3.50)
ON CONFLICT (id) DO UPDATE SET
  order_id = EXCLUDED.order_id, product_id = EXCLUDED.product_id,
  quantity = EXCLUDED.quantity, unit_price = EXCLUDED.unit_price;

-- Payments (matching paid orders)
INSERT INTO payments (id, order_id, stripe_payment_id, status, amount, attempts, created_at, updated_at) VALUES
(9001, 9001, 'pi_dev_9001', 'SUCCEEDED', 20.90, 1, NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'),
(9002, 9002, 'pi_dev_9002', 'SUCCEEDED', 16.60, 1, NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'),
(9003, 9003, 'pi_dev_9003', 'SUCCEEDED', 25.10, 1, NOW() - INTERVAL '1 day',  NOW() - INTERVAL '1 day'),
(9004, 9004, 'pi_dev_9004', 'SUCCEEDED', 12.30, 1, NOW() - INTERVAL '1 day',  NOW() - INTERVAL '1 day'),
(9005, 9005, 'pi_dev_9005', 'SUCCEEDED', 18.40, 1, NOW() - INTERVAL '12 hours', NOW() - INTERVAL '12 hours'),
(9006, 9006, 'pi_dev_9006', 'SUCCEEDED', 31.70, 1, NOW() - INTERVAL '6 hours',  NOW() - INTERVAL '6 hours')
ON CONFLICT (id) DO UPDATE SET
  stripe_payment_id = EXCLUDED.stripe_payment_id, status = EXCLUDED.status,
  amount = EXCLUDED.amount, attempts = EXCLUDED.attempts;
