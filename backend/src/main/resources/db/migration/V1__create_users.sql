CREATE TABLE users (
    user_id         BIGSERIAL PRIMARY KEY,
    username        VARCHAR(60) UNIQUE NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    full_name       TEXT,
    role            VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'RECORDER', 'VIEWER')),
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_users_username ON users(username);
