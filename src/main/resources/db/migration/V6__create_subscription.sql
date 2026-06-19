CREATE TABLE subscription (
    subscription_id BIGSERIAL           NOT NULL,
    user_id         UUID                NOT NULL,
    event_id        UUID                NOT NULL,
    status          VARCHAR(50)         NOT NULL,
    subscribed_at   TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    unsubscribed_at TIMESTAMP,

    CONSTRAINT pk_subscription              PRIMARY KEY (subscription_id),
    CONSTRAINT uq_subscription_user_event   UNIQUE (user_id, event_id),
    CONSTRAINT fk_subscription_user         FOREIGN KEY (user_id)  REFERENCES users (id),
    CONSTRAINT fk_subscription_event        FOREIGN KEY (event_id) REFERENCES events (id)
);

CREATE INDEX idx_subscription_user_id  ON subscription (user_id);
CREATE INDEX idx_subscription_event_id ON subscription (event_id);
CREATE INDEX idx_subscription_status   ON subscription (status);
