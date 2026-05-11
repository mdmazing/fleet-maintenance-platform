package com.fleet.maintenance.dto.request;

import com.fleet.maintenance.domain.TriggerReason;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateMaintenanceOrderRequest {

    @NotNull
    private Long assetId;

    private Long technicianId;

    @Builder.Default
    private TriggerReason triggerReason = TriggerReason.MANUAL;

    @NotBlank
    private String description;

    private LocalDate scheduledDate;  // defaults to now+7 in service if null

    private String notes;
}
