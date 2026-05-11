package com.fleet.maintenance.controller;

import com.fleet.maintenance.domain.FaultSeverity;
import com.fleet.maintenance.dto.request.CreateFaultLogRequest;
import com.fleet.maintenance.dto.response.FaultLogResponse;
import com.fleet.maintenance.service.FaultLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fault-logs")
@RequiredArgsConstructor
@Tag(name = "Fault Logs", description = "Fault log management endpoints")
public class FaultLogController {
    private final FaultLogService faultLogService;

    @PostMapping
    @Operation(summary = "Create a new fault log")
    public ResponseEntity<FaultLogResponse> create(@Valid @RequestBody CreateFaultLogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(faultLogService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get fault log by ID")
    public ResponseEntity<FaultLogResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(faultLogService.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all fault logs")
    public ResponseEntity<List<FaultLogResponse>> getAll() {
        return ResponseEntity.ok(faultLogService.getAll());
    }

    @GetMapping("/asset/{assetId}")
    @Operation(summary = "Get fault logs for an asset")
    public ResponseEntity<List<FaultLogResponse>> getByAssetId(@PathVariable Long assetId) {
        return ResponseEntity.ok(faultLogService.getByAssetId(assetId));
    }

    @GetMapping("/unresolved")
    @Operation(summary = "Get unresolved fault logs")
    public ResponseEntity<List<FaultLogResponse>> getUnresolved() {
        return ResponseEntity.ok(faultLogService.getUnresolved());
    }

    @GetMapping("/severity/{severity}")
    @Operation(summary = "Get fault logs by severity")
    public ResponseEntity<List<FaultLogResponse>> getBySeverity(@PathVariable FaultSeverity severity) {
        return ResponseEntity.ok(faultLogService.getBySeverity(severity));
    }

    @PutMapping("/{id}/resolve")
    @Operation(summary = "Resolve fault log")
    public ResponseEntity<FaultLogResponse> resolve(@PathVariable Long id) {
        return ResponseEntity.ok(faultLogService.resolve(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete fault log")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        faultLogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
