CREATE TABLE assets (
    id                    BIGSERIAL PRIMARY KEY,
    serial_number         VARCHAR(100) NOT NULL UNIQUE,
    type                  VARCHAR(50)  NOT NULL,
    location              VARCHAR(200) NOT NULL,
    operating_hours       DOUBLE PRECISION NOT NULL DEFAULT 0,
    status                VARCHAR(30)  NOT NULL DEFAULT 'OPERATIONAL',
    last_maintenance_date DATE,
    next_maintenance_due  DATE,
    created_at            TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_assets_status ON assets (status);
CREATE INDEX idx_assets_type   ON assets (type);
