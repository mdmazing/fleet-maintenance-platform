package com.fleet.maintenance.controller;

import com.fleet.maintenance.domain.AssetStatus;
import com.fleet.maintenance.dto.request.CreateAssetRequest;
import com.fleet.maintenance.dto.request.UpdateAssetRequest;
import com.fleet.maintenance.dto.request.UpdateOperatingHoursRequest;
import com.fleet.maintenance.dto.response.AssetResponse;
import com.fleet.maintenance.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@Tag(name = "Assets", description = "Asset lifecycle management")
public class AssetController {

    private final AssetService assetService;

    @PostMapping
    @Operation(summary = "Register a new asset")
    public ResponseEntity<AssetResponse> create(@Valid @RequestBody CreateAssetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assetService.create(request));
    }

    @GetMapping
    @Operation(summary = "List all assets")
    public ResponseEntity<List<AssetResponse>> getAll() {
        return ResponseEntity.ok(assetService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get asset by ID")
    public ResponseEntity<AssetResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(assetService.getById(id));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Filter assets by status")
    public ResponseEntity<List<AssetResponse>> getByStatus(@PathVariable AssetStatus status) {
        return ResponseEntity.ok(assetService.getByStatus(status));
    }

    @GetMapping("/overdue")
    @Operation(summary = "List assets overdue for maintenance")
    public ResponseEntity<List<AssetResponse>> getOverdue() {
        return ResponseEntity.ok(assetService.getOverdueAssets());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update asset location, status, and maintenance dates")
    public ResponseEntity<AssetResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAssetRequest request) {
        return ResponseEntity.ok(assetService.update(id, request));
    }

    @PatchMapping("/{id}/operating-hours")
    @Operation(summary = "Update operating hours — triggers scheduler checks on next cycle")
    public ResponseEntity<AssetResponse> updateOperatingHours(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOperatingHoursRequest request) {
        return ResponseEntity.ok(assetService.updateOperatingHours(id, request.getOperatingHours()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an asset")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        assetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
