CREATE TABLE office_staff (
    office_id    BIGSERIAL PRIMARY KEY,
    full_name    TEXT NOT NULL,
    position     VARCHAR(120),
    phone        VARCHAR(40),
    member_id    BIGINT REFERENCES members(member_id),
    clergy_id    BIGINT REFERENCES clergy(clergy_id),
    is_active    BOOLEAN DEFAULT TRUE,
    created_at   TIMESTAMP DEFAULT NOW(),
    updated_at   TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_office_staff_name ON office_staff(full_name);
CREATE INDEX idx_office_staff_phone ON office_staff(phone);
CREATE INDEX idx_office_staff_member ON office_staff(member_id);
CREATE INDEX idx_office_staff_clergy ON office_staff(clergy_id);
