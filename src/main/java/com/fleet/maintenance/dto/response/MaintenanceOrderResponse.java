package com.fleet.maintenance.dto.response;

import com.fleet.maintenance.domain.MaintenanceOrder;
import com.fleet.maintenance.domain.OrderStatus;
import com.fleet.maintenance.domain.TriggerReason;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder
public class MaintenanceOrderResponse {

    private Long id;
    private String orderNumber;
    private Long assetId;
    private String assetSerialNumber;
    private Long technicianId;
    private String technicianName;
    private OrderStatus status;
    private TriggerReason triggerReason;
    private String description;
    private LocalDate scheduledDate;
    private LocalDate completedDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MaintenanceOrderResponse from(MaintenanceOrder o) {
        return MaintenanceOrderResponse.builder()
                .id(o.getId())
                .orderNumber(o.getOrderNumber())
                .assetId(o.getAsset().getId())
                .assetSerialNumber(o.getAsset().getSerialNumber())
                .technicianId(o.getTechnician() != null ? o.getTechnician().getId() : null)
                .technicianName(o.getTechnician() != null
                        ? o.getTechnician().getFirstName() + " " + o.getTechnician().getLastName()
                        : null)
                .status(o.getStatus())
                .triggerReason(o.getTriggerReason())
                .description(o.getDescription())
                .scheduledDate(o.getScheduledDate())
                .completedDate(o.getCompletedDate())
                .notes(o.getNotes())
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }
}
