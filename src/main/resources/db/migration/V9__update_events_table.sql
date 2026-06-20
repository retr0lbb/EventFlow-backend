-- Remove cnpj e sua constraint de unicidade
ALTER TABLE events
    DROP CONSTRAINT uq_events_cnpj;

ALTER TABLE events
    DROP COLUMN cnpj;

-- Remove latitude e longitude
ALTER TABLE events
    DROP COLUMN latitude;

ALTER TABLE events
    DROP COLUMN longitude;

-- Converte starts_at e ends_at de INT para TIMESTAMP (drop + recria, sem dados a preservar)
ALTER TABLE events
    DROP COLUMN starts_at;

ALTER TABLE events
    ADD COLUMN starts_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE events
    DROP COLUMN ends_at;

ALTER TABLE events
    ADD COLUMN ends_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- Adiciona banner_url
ALTER TABLE events
    ADD COLUMN banner_url VARCHAR(500);