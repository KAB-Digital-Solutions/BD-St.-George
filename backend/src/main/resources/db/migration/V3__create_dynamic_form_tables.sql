CREATE TABLE form_field_definitions (
    id                BIGSERIAL PRIMARY KEY,
    module_key        VARCHAR(60)  NOT NULL,
    field_key         VARCHAR(100) NOT NULL,
    label_am          TEXT         NOT NULL,
    label_en          TEXT,
    field_type        VARCHAR(30)  NOT NULL,
    dropdown_opts     JSONB,
    validation_rules  JSONB,
    is_required       BOOLEAN DEFAULT FALSE,
    is_active         BOOLEAN DEFAULT TRUE,
    is_system_core    BOOLEAN DEFAULT FALSE,
    sort_order        INTEGER      NOT NULL,
    created_at        TIMESTAMP DEFAULT NOW(),
    UNIQUE (module_key, field_key)
);

CREATE TABLE record_custom_fields (
    id          BIGSERIAL PRIMARY KEY,
    module_key  VARCHAR(60)  NOT NULL,
    record_id   BIGINT       NOT NULL,
    field_key   VARCHAR(100) NOT NULL,
    field_value TEXT,
    created_at  TIMESTAMP DEFAULT NOW(),
    UNIQUE (module_key, record_id, field_key)
);

CREATE INDEX idx_form_fields_module ON form_field_definitions(module_key, is_active, sort_order);
CREATE INDEX idx_custom_fields_record ON record_custom_fields(module_key, record_id);
