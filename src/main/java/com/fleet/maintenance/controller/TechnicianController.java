package com.fleet.maintenance.controller;

import com.fleet.maintenance.dto.request.CreateTechnicianRequest;
import com.fleet.maintenance.dto.response.TechnicianResponse;
import com.fleet.maintenance.service.TechnicianService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/technicians")
@RequiredArgsConstructor
@Tag(name = "Technicians", description = "Technician management endpoints")
public class TechnicianController {
    private final TechnicianService technicianService;

    @PostMapping
    @Operation(summary = "Create a new technician")
    public ResponseEntity<TechnicianResponse> create(@Valid @RequestBody CreateTechnicianRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(technicianService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get technician by ID")
    public ResponseEntity<TechnicianResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(technicianService.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all technicians")
    public ResponseEntity<List<TechnicianResponse>> getAll() {
        return ResponseEntity.ok(technicianService.getAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update technician")
    public ResponseEntity<TechnicianResponse> update(@PathVariable Long id, @Valid @RequestBody CreateTechnicianRequest request) {
        return ResponseEntity.ok(technicianService.update(id, request));
    }

    @PutMapping("/{id}/availability/{available}")
    @Operation(summary = "Update technician availability")
    public ResponseEntity<Void> updateAvailability(@PathVariable Long id, @PathVariable Boolean available) {
        technicianService.setAvailable(id, available);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete technician")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        technicianService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
