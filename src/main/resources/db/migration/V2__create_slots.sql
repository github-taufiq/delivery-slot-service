CREATE TABLE slots (
    slot_id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    postcode           TEXT        NOT NULL,
    warehouse_id       UUID        NOT NULL REFERENCES warehouses (warehouse_id),
    start_at           TIMESTAMPTZ NOT NULL,
    end_at             TIMESTAMPTZ NOT NULL,
    total_capacity     INTEGER     NOT NULL,
    remaining_capacity INTEGER     NOT NULL
);
