CREATE TABLE baptisms (
    baptism_id              BIGSERIAL PRIMARY KEY,
    member_id               BIGINT NOT NULL REFERENCES members(member_id) ON DELETE CASCADE,
    child_name              TEXT NOT NULL,
    baptism_date            DATE,
    officiating_clergy_id   BIGINT REFERENCES clergy(clergy_id),
    church_name             TEXT,
    is_active               BOOLEAN DEFAULT TRUE,
    created_at              TIMESTAMP DEFAULT NOW(),
    updated_at              TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_baptisms_member ON baptisms(member_id);
CREATE INDEX idx_baptisms_child_name ON baptisms(child_name);
CREATE INDEX idx_baptisms_date ON baptisms(baptism_date);
CREATE INDEX idx_baptisms_clergy ON baptisms(officiating_clergy_id);
