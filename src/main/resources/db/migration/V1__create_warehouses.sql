CREATE TABLE warehouses (
    warehouse_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         TEXT NOT NULL,
    timezone     TEXT NOT NULL
);

COMMENT ON COLUMN warehouses.timezone IS 'IANA zone of the warehouse, e.g. Europe/Amsterdam';
