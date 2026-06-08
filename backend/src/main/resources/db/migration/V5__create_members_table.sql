CREATE TABLE members (
    member_id        BIGSERIAL PRIMARY KEY,
    full_name        TEXT NOT NULL,
    phone            VARCHAR(40),
    photo_path       VARCHAR(500),
    kebele           VARCHAR(120),
    clergy_id        BIGINT,
    registered_date  DATE,
    is_active        BOOLEAN DEFAULT TRUE,
    status           VARCHAR(30) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'TRANSFERRED', 'DECEASED')),
    second_member    JSONB,
    created_at       TIMESTAMP DEFAULT NOW(),
    updated_at       TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_members_name ON members(full_name);
CREATE INDEX idx_members_phone ON members(phone);
CREATE INDEX idx_members_kebele ON members(kebele);
CREATE INDEX idx_members_clergy ON members(clergy_id);
