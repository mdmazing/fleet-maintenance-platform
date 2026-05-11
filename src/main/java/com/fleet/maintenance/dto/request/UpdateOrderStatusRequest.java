package com.fleet.maintenance.dto.request;

import com.fleet.maintenance.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateOrderStatusRequest {

    @NotNull
    private OrderStatus status;

    private Long technicianId;
    private String notes;
}
