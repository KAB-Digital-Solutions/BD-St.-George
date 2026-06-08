CREATE TABLE abnet_school (
    abnet_id     BIGSERIAL PRIMARY KEY,
    full_name    TEXT NOT NULL,
    phone        VARCHAR(40),
    birth_date   DATE,
    gender       VARCHAR(20),
    member_id    BIGINT REFERENCES members(member_id),
    clergy_id    BIGINT REFERENCES clergy(clergy_id),
    is_active    BOOLEAN DEFAULT TRUE,
    created_at   TIMESTAMP DEFAULT NOW(),
    updated_at   TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_abnet_school_name ON abnet_school(full_name);
CREATE INDEX idx_abnet_school_member ON abnet_school(member_id);
CREATE INDEX idx_abnet_school_clergy ON abnet_school(clergy_id);
