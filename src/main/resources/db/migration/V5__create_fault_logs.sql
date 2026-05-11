CREATE TABLE fault_logs (
    id                   BIGSERIAL PRIMARY KEY,
    asset_id             BIGINT       NOT NULL REFERENCES assets(id),
    reported_by          VARCHAR(150) NOT NULL,
    severity             VARCHAR(20)  NOT NULL,
    description          VARCHAR(1000) NOT NULL,
    reported_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    resolved             BOOLEAN      NOT NULL DEFAULT FALSE,
    resolved_at          TIMESTAMP,
    maintenance_order_id BIGINT       REFERENCES maintenance_orders(id)
);

CREATE INDEX idx_fault_logs_asset_id  ON fault_logs (asset_id);
CREATE INDEX idx_fault_logs_resolved  ON fault_logs (resolved);
CREATE INDEX idx_fault_logs_severity  ON fault_logs (severity);
