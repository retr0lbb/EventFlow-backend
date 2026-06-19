CREATE TABLE checkin (
    checkin_id          BIGSERIAL   NOT NULL,
    subscription_id     BIGINT      NOT NULL,
    event_session_id    BIGINT,
    checked_in_at       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at          TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_checkin               PRIMARY KEY (checkin_id),
    CONSTRAINT fk_checkin_subscription  FOREIGN KEY (subscription_id)  REFERENCES subscription (subscription_id),
    CONSTRAINT fk_checkin_event_session FOREIGN KEY (event_session_id) REFERENCES event_sessions (event_session_id)
);

CREATE INDEX idx_checkin_subscription_id  ON checkin (subscription_id);
CREATE INDEX idx_checkin_event_session_id ON checkin (event_session_id);
CREATE INDEX idx_checkin_checked_in_at    ON checkin (checked_in_at);