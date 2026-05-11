package com.fleet.maintenance.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class UpdateOperatingHoursRequest {

    @PositiveOrZero
    private double operatingHours;
}
