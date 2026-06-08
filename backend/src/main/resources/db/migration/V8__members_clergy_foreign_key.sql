ALTER TABLE members
    ADD CONSTRAINT fk_members_clergy
    FOREIGN KEY (clergy_id) REFERENCES clergy(clergy_id);
