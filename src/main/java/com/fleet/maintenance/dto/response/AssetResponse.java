package com.fleet.maintenance.dto.response;

import com.fleet.maintenance.domain.Asset;
import com.fleet.maintenance.domain.AssetStatus;
import com.fleet.maintenance.domain.AssetType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder
public class AssetResponse {

    private Long id;
    private String serialNumber;
    private AssetType type;
    private String location;
    private double operatingHours;
    private AssetStatus status;
    private LocalDate lastMaintenanceDate;
    private LocalDate nextMaintenanceDue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AssetResponse from(Asset a) {
        return AssetResponse.builder()
                .id(a.getId())
                .serialNumber(a.getSerialNumber())
                .type(a.getType())
                .location(a.getLocation())
                .operatingHours(a.getOperatingHours())
                .status(a.getStatus())
                .lastMaintenanceDate(a.getLastMaintenanceDate())
                .nextMaintenanceDue(a.getNextMaintenanceDue())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}
