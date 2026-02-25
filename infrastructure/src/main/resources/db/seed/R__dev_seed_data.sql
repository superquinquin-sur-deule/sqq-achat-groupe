-- Dev Seed Data for Group Purchasing Platform
-- This file is reloaded on every dev restart (Flyway repeatable migration)

-- Clear existing data (order matters due to FK constraints)
TRUNCATE payments, order_items, orders, time_slots, products, ventes RESTART IDENTITY CASCADE;

-- ============================================================================
-- VENTES (Sales Events)
-- Uses dynamic dates relative to CURRENT_DATE to ensure there's always an active vente
-- ============================================================================
INSERT INTO ventes (id, name, description, status, start_date, end_date, created_at) VALUES
(1, 'Vente de Saison', 'Grande vente de produits bio de saison - fruits, legumes et produits laitiers locaux', 'ACTIVE',
    (CURRENT_DATE - INTERVAL '7 days')::timestamptz,
    (CURRENT_DATE + INTERVAL '30 days')::timestamptz,
    (CURRENT_TIMESTAMP - INTERVAL '14 days')::timestamptz),
(2, 'Vente Precedente', 'Vente speciale terminee avec produits festifs et gourmands', 'CLOSED',
    (CURRENT_DATE - INTERVAL '60 days')::timestamptz,
    (CURRENT_DATE - INTERVAL '30 days')::timestamptz,
    (CURRENT_TIMESTAMP - INTERVAL '75 days')::timestamptz),
(3, 'Vente a Venir', 'Produits frais pour la prochaine saison', 'ACTIVE',
    (CURRENT_DATE + INTERVAL '14 days')::timestamptz,
    (CURRENT_DATE + INTERVAL '60 days')::timestamptz,
    CURRENT_TIMESTAMP::timestamptz);

SELECT setval('ventes_id_seq', 3);

-- ============================================================================
-- PRODUCTS
-- ============================================================================
-- Products for Vente 1 (Printemps Bio 2024)
INSERT INTO products (id, vente_id, name, description, prix_ht, taux_tva, supplier, stock, active, version, reference, category, brand) VALUES
(1, 1, 'Panier de legumes bio', 'Assortiment de legumes de saison: carottes, poireaux, choux, navets (environ 5kg)', 23.70, 5.50, 'Ferme du Soleil', 50, true, 0, 'LEG-001', 'Legumes', 'Ferme du Soleil'),
(2, 1, 'Pommes Golden bio', 'Pommes Golden issues de vergers locaux - caisse de 3kg', 11.85, 5.50, 'Vergers de la Vallee', 30, true, 0, 'FRU-001', 'Fruits', 'Vergers de la Vallee'),
(3, 1, 'Oeufs fermiers bio', 'Boite de 12 oeufs de poules elevees en plein air', 6.16, 5.50, 'Ferme du Soleil', 100, true, 0, 'OEU-001', 'Oeufs et produits laitiers', 'Ferme du Soleil'),
(4, 1, 'Fromage de chevre frais', 'Fromage de chevre artisanal - 200g', 8.44, 5.50, 'Chevrerie des Monts', 40, true, 0, 'FRO-001', 'Oeufs et produits laitiers', 'Chevrerie des Monts'),
(5, 1, 'Miel de fleurs bio', 'Pot de miel toutes fleurs - 500g', 13.27, 5.50, 'Rucher du Vallon', 25, true, 0, 'MIE-001', 'Epicerie', 'Rucher du Vallon'),
(6, 1, 'Pain de campagne bio', 'Pain au levain naturel - 800g', 5.21, 5.50, 'Boulangerie Artisanale', 60, true, 0, 'PAI-001', 'Boulangerie', 'Boulangerie Artisanale'),
(7, 1, 'Jus de pomme artisanal', 'Bouteille de jus de pomme pur - 1L', 4.27, 5.50, 'Vergers de la Vallee', 80, true, 0, 'JUS-001', 'Boissons', 'Vergers de la Vallee'),
(8, 1, 'Yaourts nature bio', 'Lot de 4 yaourts nature au lait entier', 3.98, 5.50, 'Laiterie du Terroir', 45, true, 0, 'YAO-001', 'Oeufs et produits laitiers', 'Laiterie du Terroir');

-- Products for Vente 2 (Panier de Noel 2023) - Closed sale
INSERT INTO products (id, vente_id, name, description, prix_ht, taux_tva, supplier, stock, active, version, reference, category, brand) VALUES
(9, 2, 'Foie gras de canard', 'Bloc de foie gras mi-cuit - 200g', 30.33, 5.50, 'Ferme des Palmipedes', 0, true, 0, 'FOI-001', 'Traiteur', 'Ferme des Palmipedes'),
(10, 2, 'Champagne brut', 'Bouteille de champagne brut - 75cl', 23.75, 20.00, 'Cave du Terroir', 0, true, 0, 'CHA-001', 'Boissons', 'Cave du Terroir'),
(11, 2, 'Buche glacee', 'Buche glacee artisanale 6 parts - chocolat/vanille', 22.75, 5.50, 'Patisserie Gourmande', 0, true, 0, 'BUC-001', 'Desserts', 'Patisserie Gourmande'),
(12, 2, 'Coffret chocolats', 'Assortiment de chocolats fins - 250g', 17.06, 5.50, 'Chocolaterie Artisanale', 0, true, 0, 'CHO-001', 'Desserts', 'Chocolaterie Artisanale'),
(13, 2, 'Saumon fume', 'Saumon fume artisanal - 200g', 14.22, 5.50, 'Fumoir du Littoral', 0, true, 0, 'SAU-001', 'Traiteur', 'Fumoir du Littoral');

-- Products for Vente 3 (Ete Gourmand 2024)
INSERT INTO products (id, vente_id, name, description, prix_ht, taux_tva, supplier, stock, active, version, reference, category, brand) VALUES
(14, 3, 'Tomates anciennes bio', 'Assortiment de tomates anciennes - caisse 2kg', 9.29, 5.50, 'Ferme du Soleil', 40, true, 0, 'TOM-001', 'Legumes', 'Ferme du Soleil'),
(15, 3, 'Courgettes bio', 'Courgettes de plein champ - lot de 1kg', 4.27, 5.50, 'Ferme du Soleil', 60, true, 0, 'COU-001', 'Legumes', 'Ferme du Soleil'),
(16, 3, 'Melon charentais', 'Melon charentais sucre - piece', 5.21, 5.50, 'Maraicher Local', 35, true, 0, 'MEL-001', 'Fruits', 'Maraicher Local'),
(17, 3, 'Saucisses artisanales', 'Lot de 6 saucisses pur porc - 600g', 11.37, 5.50, 'Boucherie du Village', 30, true, 0, 'SAU-002', 'Viandes', 'Boucherie du Village'),
(18, 3, 'Cotes de porc', 'Lot de 4 cotes de porc fermier', 15.64, 5.50, 'Boucherie du Village', 25, true, 0, 'COT-001', 'Viandes', 'Boucherie du Village'),
(19, 3, 'Peches bio', 'Barquette de peches jaunes - 1kg', 7.49, 5.50, 'Vergers de la Vallee', 45, true, 0, 'PEC-001', 'Fruits', 'Vergers de la Vallee'),
(20, 3, 'Glaces artisanales', 'Pot de glace artisanale 500ml - parfum au choix', 8.06, 5.50, 'Glacier du Terroir', 50, true, 0, 'GLA-001', 'Desserts', 'Glacier du Terroir'),
(21, 3, 'Vin rose local', 'Bouteille de rose AOC - 75cl', 7.50, 20.00, 'Cave du Terroir', 40, true, 0, 'VIN-001', 'Boissons', 'Cave du Terroir'),
(22, 3, 'Salade mesclun', 'Sachet de mesclun frais - 200g', 3.32, 5.50, 'Ferme du Soleil', 70, false, 0, 'SAL-001', 'Legumes', 'Ferme du Soleil');

SELECT setval('products_id_seq', 22);

-- ============================================================================
-- TIME SLOTS
-- Uses dynamic dates relative to CURRENT_DATE
-- ============================================================================
-- Time slots for Vente 1 (Active sale) - Some in the past (with orders), some available
INSERT INTO time_slots (id, vente_id, date, start_time, end_time, capacity, reserved, version) VALUES
(1, 1, CURRENT_DATE - INTERVAL '3 days', '09:00', '10:00', 20, 15, 0),
(2, 1, CURRENT_DATE - INTERVAL '3 days', '10:00', '11:00', 20, 20, 0),
(3, 1, CURRENT_DATE - INTERVAL '3 days', '11:00', '12:00', 20, 8, 0),
(4, 1, CURRENT_DATE - INTERVAL '2 days', '09:00', '10:00', 25, 10, 0),
(5, 1, CURRENT_DATE - INTERVAL '2 days', '10:00', '11:00', 25, 5, 0),
(6, 1, CURRENT_DATE - INTERVAL '2 days', '14:00', '15:00', 15, 0, 0),
-- Future slots available for new orders
(7, 1, CURRENT_DATE + INTERVAL '3 days', '09:00', '10:00', 20, 0, 0),
(8, 1, CURRENT_DATE + INTERVAL '3 days', '10:00', '11:00', 20, 2, 0),
(9, 1, CURRENT_DATE + INTERVAL '3 days', '14:00', '15:00', 15, 0, 0),
(10, 1, CURRENT_DATE + INTERVAL '4 days', '09:00', '10:00', 25, 0, 0),
(11, 1, CURRENT_DATE + INTERVAL '4 days', '10:00', '11:00', 25, 0, 0),
(12, 1, CURRENT_DATE + INTERVAL '5 days', '09:00', '10:00', 20, 0, 0);

-- Time slots for Vente 2 (Closed sale) - All in the past, fully reserved
INSERT INTO time_slots (id, vente_id, date, start_time, end_time, capacity, reserved, version) VALUES
(13, 2, CURRENT_DATE - INTERVAL '35 days', '09:00', '10:00', 30, 30, 0),
(14, 2, CURRENT_DATE - INTERVAL '35 days', '10:00', '11:00', 30, 30, 0),
(15, 2, CURRENT_DATE - INTERVAL '35 days', '14:00', '15:00', 30, 28, 0),
(16, 2, CURRENT_DATE - INTERVAL '34 days', '09:00', '10:00', 30, 30, 0);

-- Time slots for Vente 3 (Future sale)
INSERT INTO time_slots (id, vente_id, date, start_time, end_time, capacity, reserved, version) VALUES
(17, 3, CURRENT_DATE + INTERVAL '20 days', '08:00', '09:00', 20, 0, 0),
(18, 3, CURRENT_DATE + INTERVAL '20 days', '09:00', '10:00', 20, 0, 0),
(19, 3, CURRENT_DATE + INTERVAL '20 days', '10:00', '11:00', 20, 0, 0),
(20, 3, CURRENT_DATE + INTERVAL '21 days', '08:00', '09:00', 25, 0, 0),
(21, 3, CURRENT_DATE + INTERVAL '21 days', '09:00', '10:00', 25, 0, 0),
(22, 3, CURRENT_DATE + INTERVAL '21 days', '10:00', '11:00', 25, 0, 0);

SELECT setval('time_slots_id_seq', 22);

-- ============================================================================
-- ORDERS
-- Uses dynamic dates and updated time slot references
-- ============================================================================
-- Orders for Vente 1 (Active sale) - Various statuses
INSERT INTO orders (id, vente_id, order_number, customer_first_name, customer_last_name, customer_email, customer_phone, time_slot_id, status, total_amount, created_at) VALUES
-- PENDING orders (awaiting payment) - for past time slots
('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 1, 'AG-2024-00001', 'Marie', 'Dupont', 'marie.dupont@email.fr', '0612345678', 1, 'PENDING', 44.00, CURRENT_TIMESTAMP - INTERVAL '5 days'),
('b2c3d4e5-f6a7-8901-bcde-f12345678901', 1, 'AG-2024-00002', 'Jean', 'Martin', 'jean.martin@email.fr', '0623456789', 1, 'PENDING', 31.40, CURRENT_TIMESTAMP - INTERVAL '4 days'),

-- PAID orders (payment received, awaiting pickup)
('c3d4e5f6-a7b8-9012-cdef-123456789012', 1, 'AG-2024-00003', 'Sophie', 'Bernard', 'sophie.bernard@email.fr', '0634567890', 2, 'PAID', 56.50, CURRENT_TIMESTAMP - INTERVAL '4 days'),
('d4e5f6a7-b8c9-0123-def0-234567890123', 1, 'AG-2024-00004', 'Pierre', 'Dubois', 'pierre.dubois@email.fr', '0645678901', 2, 'PAID', 25.00, CURRENT_TIMESTAMP - INTERVAL '4 days'),
('e5f6a7b8-c9d0-1234-ef01-345678901234', 1, 'AG-2024-00005', 'Isabelle', 'Moreau', 'isabelle.moreau@email.fr', '0656789012', 3, 'PAID', 68.90, CURRENT_TIMESTAMP - INTERVAL '3 days'),

-- PICKED_UP orders (completed)
('f6a7b8c9-d0e1-2345-f012-456789012345', 1, 'AG-2024-00006', 'Francois', 'Petit', 'francois.petit@email.fr', '0667890123', 4, 'PICKED_UP', 37.50, CURRENT_TIMESTAMP - INTERVAL '3 days'),
('a7b8c9d0-e1f2-3456-0123-567890123456', 1, 'AG-2024-00007', 'Catherine', 'Leroy', 'catherine.leroy@email.fr', '0678901234', 4, 'PICKED_UP', 49.40, CURRENT_TIMESTAMP - INTERVAL '3 days'),

-- CANCELLED order
('b8c9d0e1-f2a3-4567-1234-678901234567', 1, 'AG-2024-00008', 'Michel', 'Roux', 'michel.roux@email.fr', '0689012345', 5, 'CANCELLED', 25.00, CURRENT_TIMESTAMP - INTERVAL '2 days'),

-- New orders for future time slots (for demo purposes)
('11111111-1111-1111-1111-111111111111', 1, 'AG-2024-00009', 'Claire', 'Fontaine', 'claire.fontaine@email.fr', '0611111111', 8, 'PAID', 37.00, CURRENT_TIMESTAMP - INTERVAL '1 day'),
('22222222-2222-2222-2222-222222222222', 1, 'AG-2024-00010', 'Marc', 'Riviere', 'marc.riviere@email.fr', '0622222222', 8, 'PENDING', 29.50, CURRENT_TIMESTAMP - INTERVAL '12 hours');

-- Orders for Vente 2 (Closed sale) - All picked up
INSERT INTO orders (id, vente_id, order_number, customer_first_name, customer_last_name, customer_email, customer_phone, time_slot_id, status, total_amount, created_at) VALUES
('c9d0e1f2-a3b4-5678-2345-789012345678', 2, 'AG-2023-00101', 'Anne', 'Richard', 'anne.richard@email.fr', '0690123456', 13, 'PICKED_UP', 84.50, CURRENT_TIMESTAMP - INTERVAL '40 days'),
('d0e1f2a3-b4c5-6789-3456-890123456789', 2, 'AG-2023-00102', 'Luc', 'Simon', 'luc.simon@email.fr', '0601234567', 13, 'PICKED_UP', 118.50, CURRENT_TIMESTAMP - INTERVAL '39 days'),
('e1f2a3b4-c5d6-7890-4567-901234567890', 2, 'AG-2023-00103', 'Nathalie', 'Blanc', 'nathalie.blanc@email.fr', '0612345670', 14, 'PICKED_UP', 56.00, CURRENT_TIMESTAMP - INTERVAL '38 days');

-- ============================================================================
-- ORDER ITEMS
-- ============================================================================
-- Items for Order AG-2024-00001 (Marie Dupont - PENDING)
INSERT INTO order_items (id, order_id, product_id, quantity, unit_price) VALUES
(1, 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 1, 1, 25.00),
(2, 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 3, 2, 6.50),
(3, 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 6, 1, 5.50);

-- Items for Order AG-2024-00002 (Jean Martin - PENDING)
INSERT INTO order_items (id, order_id, product_id, quantity, unit_price) VALUES
(4, 'b2c3d4e5-f6a7-8901-bcde-f12345678901', 2, 1, 12.50),
(5, 'b2c3d4e5-f6a7-8901-bcde-f12345678901', 4, 1, 8.90),
(6, 'b2c3d4e5-f6a7-8901-bcde-f12345678901', 7, 2, 4.50);

-- Items for Order AG-2024-00003 (Sophie Bernard - PAID)
INSERT INTO order_items (id, order_id, product_id, quantity, unit_price) VALUES
(7, 'c3d4e5f6-a7b8-9012-cdef-123456789012', 1, 1, 25.00),
(8, 'c3d4e5f6-a7b8-9012-cdef-123456789012', 5, 1, 14.00),
(9, 'c3d4e5f6-a7b8-9012-cdef-123456789012', 8, 2, 4.20),
(10, 'c3d4e5f6-a7b8-9012-cdef-123456789012', 7, 2, 4.50);

-- Items for Order AG-2024-00004 (Pierre Dubois - PAID)
INSERT INTO order_items (id, order_id, product_id, quantity, unit_price) VALUES
(11, 'd4e5f6a7-b8c9-0123-def0-234567890123', 1, 1, 25.00);

-- Items for Order AG-2024-00005 (Isabelle Moreau - PAID)
INSERT INTO order_items (id, order_id, product_id, quantity, unit_price) VALUES
(12, 'e5f6a7b8-c9d0-1234-ef01-345678901234', 1, 1, 25.00),
(13, 'e5f6a7b8-c9d0-1234-ef01-345678901234', 4, 2, 8.90),
(14, 'e5f6a7b8-c9d0-1234-ef01-345678901234', 5, 1, 14.00),
(15, 'e5f6a7b8-c9d0-1234-ef01-345678901234', 3, 1, 6.50),
(16, 'e5f6a7b8-c9d0-1234-ef01-345678901234', 6, 1, 5.50);

-- Items for Order AG-2024-00006 (Francois Petit - PICKED_UP)
INSERT INTO order_items (id, order_id, product_id, quantity, unit_price) VALUES
(17, 'f6a7b8c9-d0e1-2345-f012-456789012345', 2, 1, 12.50),
(18, 'f6a7b8c9-d0e1-2345-f012-456789012345', 1, 1, 25.00);

-- Items for Order AG-2024-00007 (Catherine Leroy - PICKED_UP)
INSERT INTO order_items (id, order_id, product_id, quantity, unit_price) VALUES
(19, 'a7b8c9d0-e1f2-3456-0123-567890123456', 1, 1, 25.00),
(20, 'a7b8c9d0-e1f2-3456-0123-567890123456', 8, 2, 4.20),
(21, 'a7b8c9d0-e1f2-3456-0123-567890123456', 6, 1, 5.50),
(22, 'a7b8c9d0-e1f2-3456-0123-567890123456', 7, 2, 4.50);

-- Items for Order AG-2024-00008 (Michel Roux - CANCELLED)
INSERT INTO order_items (id, order_id, product_id, quantity, unit_price) VALUES
(23, 'b8c9d0e1-f2a3-4567-1234-678901234567', 1, 1, 25.00);

-- Items for Noel orders (Vente 2)
INSERT INTO order_items (id, order_id, product_id, quantity, unit_price) VALUES
(24, 'c9d0e1f2-a3b4-5678-2345-789012345678', 9, 1, 32.00),
(25, 'c9d0e1f2-a3b4-5678-2345-789012345678', 10, 1, 28.50),
(26, 'c9d0e1f2-a3b4-5678-2345-789012345678', 12, 1, 18.00),
(27, 'c9d0e1f2-a3b4-5678-2345-789012345678', 6, 1, 6.00);

INSERT INTO order_items (id, order_id, product_id, quantity, unit_price) VALUES
(28, 'd0e1f2a3-b4c5-6789-3456-890123456789', 9, 2, 32.00),
(29, 'd0e1f2a3-b4c5-6789-3456-890123456789', 10, 1, 28.50),
(30, 'd0e1f2a3-b4c5-6789-3456-890123456789', 11, 1, 24.00);

INSERT INTO order_items (id, order_id, product_id, quantity, unit_price) VALUES
(31, 'e1f2a3b4-c5d6-7890-4567-901234567890', 9, 1, 32.00),
(32, 'e1f2a3b4-c5d6-7890-4567-901234567890', 11, 1, 24.00);

-- Items for new orders on future time slots (Vente 1)
INSERT INTO order_items (id, order_id, product_id, quantity, unit_price) VALUES
(33, '11111111-1111-1111-1111-111111111111', 1, 1, 25.00),
(34, '11111111-1111-1111-1111-111111111111', 2, 1, 12.00);

INSERT INTO order_items (id, order_id, product_id, quantity, unit_price) VALUES
(35, '22222222-2222-2222-2222-222222222222', 3, 2, 6.50),
(36, '22222222-2222-2222-2222-222222222222', 6, 1, 5.50),
(37, '22222222-2222-2222-2222-222222222222', 7, 2, 4.50);

SELECT setval('order_items_id_seq', 37);

-- ============================================================================
-- PAYMENTS
-- Uses dynamic dates
-- ============================================================================
INSERT INTO payments (id, order_id, amount, stripe_payment_id, status, attempts, created_at, updated_at, version) VALUES
-- PAID orders - successful payments
(1, 'c3d4e5f6-a7b8-9012-cdef-123456789012', 56.50, 'pi_3Oabc123def456', 'SUCCEEDED', 1, CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '4 days', 0),
(2, 'd4e5f6a7-b8c9-0123-def0-234567890123', 25.00, 'pi_3Oabc123def457', 'SUCCEEDED', 1, CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '4 days', 0),
(3, 'e5f6a7b8-c9d0-1234-ef01-345678901234', 68.90, 'pi_3Oabc123def458', 'SUCCEEDED', 1, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days', 0),

-- PICKED_UP orders - successful payments
(4, 'f6a7b8c9-d0e1-2345-f012-456789012345', 37.50, 'pi_3Oabc123def459', 'SUCCEEDED', 1, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days', 0),
(5, 'a7b8c9d0-e1f2-3456-0123-567890123456', 49.40, 'pi_3Oabc123def460', 'SUCCEEDED', 1, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days', 0),

-- PENDING orders - pending payments
(6, 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 44.00, NULL, 'PENDING', 0, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days', 0),
(7, 'b2c3d4e5-f6a7-8901-bcde-f12345678901', 31.40, NULL, 'PENDING', 0, CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '4 days', 0),

-- CANCELLED order - failed payment
(8, 'b8c9d0e1-f2a3-4567-1234-678901234567', 25.00, NULL, 'FAILED', 3, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days', 0),

-- Closed sale orders (Vente 2) - all successful
(9, 'c9d0e1f2-a3b4-5678-2345-789012345678', 84.50, 'pi_3Noel123def461', 'SUCCEEDED', 1, CURRENT_TIMESTAMP - INTERVAL '40 days', CURRENT_TIMESTAMP - INTERVAL '40 days', 0),
(10, 'd0e1f2a3-b4c5-6789-3456-890123456789', 118.50, 'pi_3Noel123def462', 'SUCCEEDED', 1, CURRENT_TIMESTAMP - INTERVAL '39 days', CURRENT_TIMESTAMP - INTERVAL '39 days', 0),
(11, 'e1f2a3b4-c5d6-7890-4567-901234567890', 56.00, 'pi_3Noel123def463', 'SUCCEEDED', 1, CURRENT_TIMESTAMP - INTERVAL '38 days', CURRENT_TIMESTAMP - INTERVAL '38 days', 0),

-- New orders for future time slots (Vente 1)
(12, '11111111-1111-1111-1111-111111111111', 37.00, 'pi_3New123def464', 'SUCCEEDED', 1, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', 0),
(13, '22222222-2222-2222-2222-222222222222', 29.50, NULL, 'PENDING', 0, CURRENT_TIMESTAMP - INTERVAL '12 hours', CURRENT_TIMESTAMP - INTERVAL '12 hours', 0);

SELECT setval('payments_id_seq', 13);
