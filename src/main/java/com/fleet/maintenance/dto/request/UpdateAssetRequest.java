package com.fleet.maintenance.dto.request;

import com.fleet.maintenance.domain.AssetStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateAssetRequest {

    @NotBlank
    private String location;

    private AssetStatus status;  // optional — if null, status is unchanged

    @PositiveOrZero
    private Double operatingHours;  // optional — if null, hours are unchanged

    private LocalDate lastMaintenanceDate;
    private LocalDate nextMaintenanceDue;
}
