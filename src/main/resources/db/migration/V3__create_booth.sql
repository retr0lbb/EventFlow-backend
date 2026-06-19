CREATE TABLE booth (
    booth_id        BIGSERIAL       NOT NULL,
    event_id        UUID            NOT NULL,
    name            VARCHAR(255)    NOT NULL,
    description     TEXT,
    map_location    VARCHAR(500),
    capacity        INT,
    deleted_at      TIMESTAMP,
    deleted_by      UUID,

    CONSTRAINT pk_booth         PRIMARY KEY (booth_id),
    CONSTRAINT fk_booth_event   FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_booth_deleted_by FOREIGN KEY (deleted_by) REFERENCES users (id)
);
 
CREATE INDEX idx_booth_event_id ON booth (event_id);
