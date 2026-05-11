package com.fleet.maintenance.dto.request;

import com.fleet.maintenance.domain.AssetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateAssetRequest {

    @NotBlank
    private String serialNumber;

    @NotNull
    private AssetType type;

    @NotBlank
    private String location;

    @PositiveOrZero
    private double operatingHours;

    private LocalDate lastMaintenanceDate;
    private LocalDate nextMaintenanceDue;
}
