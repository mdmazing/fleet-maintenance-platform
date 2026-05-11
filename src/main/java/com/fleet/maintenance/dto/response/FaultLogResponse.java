package com.fleet.maintenance.dto.response;

import com.fleet.maintenance.domain.FaultLog;
import com.fleet.maintenance.domain.FaultSeverity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class FaultLogResponse {

    private Long id;
    private Long assetId;
    private String assetSerialNumber;
    private String reportedBy;
    private FaultSeverity severity;
    private String description;
    private LocalDateTime reportedAt;
    private boolean resolved;
    private LocalDateTime resolvedAt;
    private Long maintenanceOrderId;

    public static FaultLogResponse from(FaultLog f) {
        return FaultLogResponse.builder()
                .id(f.getId())
                .assetId(f.getAsset().getId())
                .assetSerialNumber(f.getAsset().getSerialNumber())
                .reportedBy(f.getReportedBy())
                .severity(f.getSeverity())
                .description(f.getDescription())
                .reportedAt(f.getReportedAt())
                .resolved(f.isResolved())
                .resolvedAt(f.getResolvedAt())
                .maintenanceOrderId(f.getMaintenanceOrder() != null ? f.getMaintenanceOrder().getId() : null)
                .build();
    }
}
