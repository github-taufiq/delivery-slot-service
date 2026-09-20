CREATE TABLE bookings (
    booking_id  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id    UUID        NOT NULL,
    customer_id UUID        NOT NULL,
    slot_id     UUID        NOT NULL REFERENCES slots (slot_id),
    state       VARCHAR(16) NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_bookings_customer_id ON bookings (customer_id);
