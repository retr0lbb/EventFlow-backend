ALTER TABLE user_event_checkin
    ADD CONSTRAINT uq_user_event_checkin_user_event UNIQUE (user_id, event_id);