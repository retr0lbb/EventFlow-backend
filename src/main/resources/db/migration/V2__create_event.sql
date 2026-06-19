CREATE TABLE event (
    event_id            BIGSERIAL       NOT NULL,
    name                VARCHAR(255)    NOT NULL,
    description         TEXT,
    banner_url          VARCHAR(500),
    location            VARCHAR(500),
    max_participants    INT,
    starts_at           TIMESTAMP       NOT NULL,
    ends_at             TIMESTAMP       NOT NULL,
    status              VARCHAR(20),
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_event PRIMARY KEY (event_id)
);