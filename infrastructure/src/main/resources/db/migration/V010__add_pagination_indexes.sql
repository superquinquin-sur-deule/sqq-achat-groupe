CREATE INDEX idx_orders_pagination ON orders (vente_id, status, created_at DESC, id DESC);
CREATE INDEX idx_orders_search ON orders (vente_id, status, lower(customer_name) text_pattern_ops);
CREATE INDEX idx_products_vente_id ON products (vente_id, active, id);
CREATE INDEX idx_timeslots_pagination ON time_slots (vente_id, date, start_time, id);
