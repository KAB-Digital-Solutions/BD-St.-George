CREATE TABLE contributions (
    contrib_id       BIGSERIAL PRIMARY KEY,
    member_id        BIGINT NOT NULL REFERENCES members(member_id),
    amount           DECIMAL(12, 2) NOT NULL,
    receipt_no       VARCHAR(60) NOT NULL UNIQUE,
    ethiopian_year   INTEGER,
    payment_date     DATE,
    is_active        BOOLEAN DEFAULT TRUE,
    created_at       TIMESTAMP DEFAULT NOW(),
    updated_at       TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_contributions_member ON contributions(member_id);
CREATE INDEX idx_contributions_year ON contributions(ethiopian_year);
CREATE INDEX idx_contributions_receipt ON contributions(receipt_no);
