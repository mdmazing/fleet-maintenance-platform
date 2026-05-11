package com.fleet.maintenance.controller;

import com.fleet.maintenance.domain.OrderStatus;
import com.fleet.maintenance.dto.request.CreateMaintenanceOrderRequest;
import com.fleet.maintenance.dto.request.UpdateOrderStatusRequest;
import com.fleet.maintenance.dto.response.MaintenanceOrderResponse;
import com.fleet.maintenance.service.MaintenanceOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance-orders")
@RequiredArgsConstructor
@Tag(name = "Maintenance Orders", description = "Maintenance order management endpoints")
public class MaintenanceOrderController {
    private final MaintenanceOrderService maintenanceOrderService;

    @PostMapping
    @Operation(summary = "Create a new maintenance order")
    public ResponseEntity<MaintenanceOrderResponse> create(@Valid @RequestBody CreateMaintenanceOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(maintenanceOrderService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get maintenance order by ID")
    public ResponseEntity<MaintenanceOrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(maintenanceOrderService.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all maintenance orders")
    public ResponseEntity<List<MaintenanceOrderResponse>> getAll() {
        return ResponseEntity.ok(maintenanceOrderService.getAll());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get maintenance orders by status")
    public ResponseEntity<List<MaintenanceOrderResponse>> getByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(maintenanceOrderService.getByStatus(status));
    }

    @GetMapping("/asset/{assetId}")
    @Operation(summary = "Get maintenance orders for an asset")
    public ResponseEntity<List<MaintenanceOrderResponse>> getByAssetId(@PathVariable Long assetId) {
        return ResponseEntity.ok(maintenanceOrderService.getByAssetId(assetId));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update maintenance order status")
    public ResponseEntity<MaintenanceOrderResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(maintenanceOrderService.updateStatus(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete maintenance order")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        maintenanceOrderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
