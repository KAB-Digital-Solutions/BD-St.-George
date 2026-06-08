CREATE TABLE family_members (
    family_id    BIGSERIAL PRIMARY KEY,
    member_id    BIGINT NOT NULL REFERENCES members(member_id) ON DELETE CASCADE,
    full_name    TEXT NOT NULL,
    age          INTEGER,
    is_active    BOOLEAN DEFAULT TRUE,
    created_at   TIMESTAMP DEFAULT NOW(),
    updated_at   TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_family_members_member ON family_members(member_id);
CREATE INDEX idx_family_members_name ON family_members(full_name);
