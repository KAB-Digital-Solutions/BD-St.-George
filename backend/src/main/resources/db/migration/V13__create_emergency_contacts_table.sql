CREATE TABLE emergency_contacts (
    contact_id   BIGSERIAL PRIMARY KEY,
    full_name    TEXT NOT NULL,
    phone        VARCHAR(40),
    address      TEXT,
    worker_id    BIGINT REFERENCES workers(worker_id),
    office_id    BIGINT REFERENCES office_staff(office_id),
    is_active    BOOLEAN DEFAULT TRUE,
    created_at   TIMESTAMP DEFAULT NOW(),
    updated_at   TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_emergency_contacts_name ON emergency_contacts(full_name);
CREATE INDEX idx_emergency_contacts_phone ON emergency_contacts(phone);
CREATE INDEX idx_emergency_contacts_worker ON emergency_contacts(worker_id);
CREATE INDEX idx_emergency_contacts_office ON emergency_contacts(office_id);
