CREATE TABLE sunday_school (
    ss_id        BIGSERIAL PRIMARY KEY,
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

CREATE INDEX idx_sunday_school_name ON sunday_school(full_name);
CREATE INDEX idx_sunday_school_member ON sunday_school(member_id);
CREATE INDEX idx_sunday_school_clergy ON sunday_school(clergy_id);
