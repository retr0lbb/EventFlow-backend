CREATE TABLE users (
    id              UUID            NOT NULL,
    name            VARCHAR(255)    NOT NULL,
    email           VARCHAR(255)    NOT NULL,
    password        VARCHAR(255)    NOT NULL,
    phone           VARCHAR(30),
    is_email_valid  BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_users       PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE role (
    role_id BIGSERIAL       NOT NULL,
    name    VARCHAR(100)    NOT NULL,

    CONSTRAINT pk_role      PRIMARY KEY (role_id),
    CONSTRAINT uq_role_name UNIQUE (name)
);

CREATE TABLE user_roles (
    user_roles_id   BIGSERIAL   NOT NULL,
    user_id         UUID        NOT NULL,
    role_id         BIGINT      NOT NULL,

    CONSTRAINT pk_user_roles            PRIMARY KEY (user_roles_id),
    CONSTRAINT uq_user_roles_user_role  UNIQUE (user_id, role_id),
    CONSTRAINT fk_user_roles_user       FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_roles_role       FOREIGN KEY (role_id) REFERENCES role (role_id)
);

CREATE INDEX idx_user_roles_user_id ON user_roles (user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles (role_id);

-- Popular tabela role com os registros iniciais
INSERT INTO role (role_id, name) VALUES (1, 'VISITANT') ON CONFLICT DO NOTHING;
INSERT INTO role (role_id, name) VALUES (2, 'STAND_CREATOR') ON CONFLICT DO NOTHING;
INSERT INTO role (role_id, name) VALUES (3, 'EVENT_CREATOR') ON CONFLICT DO NOTHING;