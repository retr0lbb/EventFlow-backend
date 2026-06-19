CREATE TABLE booth (
    booth_id        BIGSERIAL       NOT NULL,
    event_id        BIGINT          NOT NULL,
    name            VARCHAR(255)    NOT NULL,
    description     TEXT,
    map_location    VARCHAR(500),
    capacity        INT,

    CONSTRAINT pk_booth         PRIMARY KEY (booth_id),
    CONSTRAINT fk_booth_event   FOREIGN KEY (event_id) REFERENCES event (event_id)
);
 
CREATE INDEX idx_booth_event_id ON booth (event_id);