CREATE TABLE transfers (
    transfer_id     BIGSERIAL PRIMARY KEY,
    member_id       BIGINT NOT NULL REFERENCES members(member_id),
    reason          TEXT,
    region          VARCHAR(120),
    diocese         VARCHAR(120),
    woreda          VARCHAR(120),
    transfer_date   DATE,
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_transfers_member ON transfers(member_id);
CREATE INDEX idx_transfers_date ON transfers(transfer_date);
