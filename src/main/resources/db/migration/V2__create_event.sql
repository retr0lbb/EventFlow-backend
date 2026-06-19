CREATE TABLE events (
    id                  UUID            NOT NULL,
    name                VARCHAR(255)    NOT NULL,
    cnpj                VARCHAR(14)     NOT NULL,
    description         TEXT,
    address             VARCHAR(500),
    latitude            DOUBLE PRECISION,
    longitude           DOUBLE PRECISION,
    max_participants    INT,
    starts_at           INT             NOT NULL,
    ends_at             INT             NOT NULL,
    status              VARCHAR(20),
    created_by          UUID            NOT NULL,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_by          UUID,
    deleted_at          TIMESTAMP,

    CONSTRAINT pk_events PRIMARY KEY (id),
    CONSTRAINT uq_events_cnpj UNIQUE (cnpj),
    CONSTRAINT fk_events_user FOREIGN KEY (created_by) REFERENCES users (id)
);

CREATE INDEX idx_events_created_by ON events (created_by);
