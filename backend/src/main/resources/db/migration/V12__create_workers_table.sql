CREATE TABLE workers (
    worker_id   BIGSERIAL PRIMARY KEY,
    full_name   TEXT NOT NULL,
    phone       VARCHAR(40),
    job_role    VARCHAR(120),
    gender      VARCHAR(20),
    is_active   BOOLEAN DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT NOW(),
    updated_at  TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_workers_name ON workers(full_name);
CREATE INDEX idx_workers_phone ON workers(phone);
CREATE INDEX idx_workers_job_role ON workers(job_role);
