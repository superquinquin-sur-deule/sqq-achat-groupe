CREATE TABLE time_slots (
    id         BIGSERIAL PRIMARY KEY,
    date       DATE      NOT NULL,
    start_time TIME      NOT NULL,
    end_time   TIME      NOT NULL,
    capacity   INT       NOT NULL CHECK (capacity > 0),
    reserved   INT       NOT NULL DEFAULT 0 CHECK (reserved >= 0)
);

CREATE INDEX idx_time_slots_date ON time_slots (date);
