CREATE TABLE parish_council (
    council_id   BIGSERIAL PRIMARY KEY,
    full_name    TEXT NOT NULL,
    position     VARCHAR(120),
    phone        VARCHAR(40),
    clergy_id    BIGINT REFERENCES clergy(clergy_id),
    is_active    BOOLEAN DEFAULT TRUE,
    created_at   TIMESTAMP DEFAULT NOW(),
    updated_at   TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_parish_council_name ON parish_council(full_name);
CREATE INDEX idx_parish_council_clergy ON parish_council(clergy_id);
