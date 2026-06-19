CREATE TABLE booth_sessions (
    booth_session_id    BIGSERIAL       NOT NULL,
    booth_id            BIGINT          NOT NULL,
    event_sessions_id   BIGINT,
    title               VARCHAR(255)    NOT NULL,
    description         TEXT,
    starts_at           TIMESTAMP       NOT NULL,
    ends_at             TIMESTAMP       NOT NULL,
    capacity            INT,
 
    CONSTRAINT pk_booth_sessions                PRIMARY KEY (booth_session_id),
    CONSTRAINT fk_booth_sessions_booth          FOREIGN KEY (booth_id)          REFERENCES booth (booth_id),
    CONSTRAINT fk_booth_sessions_event_session  FOREIGN KEY (event_sessions_id) REFERENCES event_sessions (event_session_id)
);
 
CREATE INDEX idx_booth_sessions_booth_id         ON booth_sessions (booth_id);
CREATE INDEX idx_booth_sessions_event_session_id ON booth_sessions (event_sessions_id);