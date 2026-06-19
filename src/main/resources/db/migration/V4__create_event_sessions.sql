CREATE TABLE event_sessions (
    event_session_id    BIGSERIAL       NOT NULL,
    event_id            UUID            NOT NULL,
    title               VARCHAR(255)    NOT NULL,
    description         TEXT,
    starts_at           TIMESTAMP       NOT NULL,
    ends_at             TIMESTAMP       NOT NULL,
    capacity            INT,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at          TIMESTAMP,
    deleted_by          UUID,
 
    CONSTRAINT pk_event_sessions        PRIMARY KEY (event_session_id),
    CONSTRAINT fk_event_sessions_event  FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_event_sessions_deleted_by FOREIGN KEY (deleted_by) REFERENCES users (id)
);
 
CREATE INDEX idx_event_sessions_event_id ON event_sessions (event_id);
