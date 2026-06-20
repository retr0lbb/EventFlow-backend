CREATE TABLE user_event_checkin (
    id              UUID            NOT NULL,
    event_id        UUID            NOT NULL,
    user_id         UUID            NOT NULL,
    status          VARCHAR(20)     NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    checked_in_at   TIMESTAMP,

    CONSTRAINT pk_user_event_checkin        PRIMARY KEY (id),
    CONSTRAINT fk_user_event_checkin_event  FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_user_event_checkin_user   FOREIGN KEY (user_id)  REFERENCES users (id)
);

CREATE INDEX idx_user_event_checkin_event_id ON user_event_checkin (event_id);
CREATE INDEX idx_user_event_checkin_user_id  ON user_event_checkin (user_id);