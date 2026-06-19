CREATE TABLE user_session_plan (
    user_session_plan_id    BIGSERIAL   NOT NULL,
    user_id                 UUID        NOT NULL,
    booth_session_id        BIGINT      NOT NULL,
    notified_at             TIMESTAMP,
    created_at              TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_user_session_plan             PRIMARY KEY (user_session_plan_id),
    CONSTRAINT uq_user_session_plan_user_booth  UNIQUE (user_id, booth_session_id),
    CONSTRAINT fk_user_session_plan_user        FOREIGN KEY (user_id)          REFERENCES users (id),
    CONSTRAINT fk_user_session_plan_booth_sess  FOREIGN KEY (booth_session_id) REFERENCES booth_sessions (booth_session_id)
);

CREATE INDEX idx_user_session_plan_user_id          ON user_session_plan (user_id);
CREATE INDEX idx_user_session_plan_booth_session_id ON user_session_plan (booth_session_id); 