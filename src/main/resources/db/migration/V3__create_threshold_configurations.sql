CREATE TABLE threshold_configurations (
    id                    BIGSERIAL PRIMARY KEY,
    asset_type            VARCHAR(50) NOT NULL UNIQUE,
    max_operating_hours   DOUBLE PRECISION NOT NULL,
    overdue_threshold_days INT NOT NULL DEFAULT 7,
    updated_at            TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Seed default thresholds for all asset types
INSERT INTO threshold_configurations (asset_type, max_operating_hours, overdue_threshold_days) VALUES
    ('VEHICLE',     500,  7),
    ('CRANE',       250,  14),
    ('COMPRESSOR',  1000, 30),
    ('GENERATOR',   750,  14),
    ('PUMP',        600,  14),
    ('OTHER',       500,  7);
