CREATE TABLE deceased (
    deceased_id        BIGSERIAL PRIMARY KEY,
    member_id          BIGINT NOT NULL UNIQUE REFERENCES members(member_id),
    death_date         DATE,
    kebele             VARCHAR(120),
    gender             VARCHAR(20),
    full_name_display  TEXT,
    created_at         TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_deceased_death_date ON deceased(death_date);
