CREATE TABLE clergy (
    clergy_id   BIGSERIAL PRIMARY KEY,
    full_name   TEXT NOT NULL,
    role_type   VARCHAR(120),
    phone       VARCHAR(40),
    address     TEXT,
    photo_path  VARCHAR(500),
    is_active   BOOLEAN DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT NOW(),
    updated_at  TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_clergy_name ON clergy(full_name);
CREATE INDEX idx_clergy_phone ON clergy(phone);
CREATE INDEX idx_clergy_role ON clergy(role_type);
