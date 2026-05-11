package com.fleet.maintenance.dto.request;

import com.fleet.maintenance.domain.FaultSeverity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateFaultLogRequest {

    @NotNull
    private Long assetId;

    @NotBlank
    private String reportedBy;

    @NotNull
    private FaultSeverity severity;

    @NotBlank
    private String description;
}
