CREATE TABLE maintenance_orders (
    id             BIGSERIAL PRIMARY KEY,
    order_number   VARCHAR(50)  NOT NULL UNIQUE,
    asset_id       BIGINT       NOT NULL REFERENCES assets(id),
    technician_id  BIGINT       REFERENCES technicians(id),
    status         VARCHAR(30)  NOT NULL DEFAULT 'PENDING',
    trigger_reason VARCHAR(30)  NOT NULL,
    description    VARCHAR(500) NOT NULL,
    scheduled_date DATE         NOT NULL,
    completed_date DATE,
    notes          VARCHAR(1000),
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_orders_asset_id ON maintenance_orders (asset_id);
CREATE INDEX idx_orders_status   ON maintenance_orders (status);
