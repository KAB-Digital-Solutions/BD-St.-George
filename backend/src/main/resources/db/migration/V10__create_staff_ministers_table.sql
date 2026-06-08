CREATE TABLE staff_ministers (
    staff_id          BIGSERIAL PRIMARY KEY,
    full_name         TEXT NOT NULL,
    gender            VARCHAR(20),
    phone             VARCHAR(40),
    hire_date         DATE,
    employment_type   VARCHAR(120),
    member_id         BIGINT REFERENCES members(member_id),
    is_active         BOOLEAN DEFAULT TRUE,
    created_at        TIMESTAMP DEFAULT NOW(),
    updated_at        TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_staff_ministers_name ON staff_ministers(full_name);
CREATE INDEX idx_staff_ministers_phone ON staff_ministers(phone);
CREATE INDEX idx_staff_ministers_member ON staff_ministers(member_id);
