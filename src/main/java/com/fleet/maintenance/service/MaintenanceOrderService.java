package com.fleet.maintenance.service;

import com.fleet.maintenance.domain.*;
import com.fleet.maintenance.dto.request.CreateMaintenanceOrderRequest;
import com.fleet.maintenance.dto.request.UpdateOrderStatusRequest;
import com.fleet.maintenance.dto.response.MaintenanceOrderResponse;
import com.fleet.maintenance.exception.ResourceNotFoundException;
import com.fleet.maintenance.repository.AssetRepository;
import com.fleet.maintenance.repository.MaintenanceOrderRepository;
import com.fleet.maintenance.repository.TechnicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MaintenanceOrderService {

    private final MaintenanceOrderRepository maintenanceOrderRepository;
    private final AssetRepository assetRepository;
    private final TechnicianRepository technicianRepository;

    public MaintenanceOrderResponse create(CreateMaintenanceOrderRequest request) {
        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found: " + request.getAssetId()));

        Technician technician = null;
        if (request.getTechnicianId() != null) {
            technician = technicianRepository.findById(request.getTechnicianId())
                    .orElseThrow(() -> new ResourceNotFoundException("Technician not found: " + request.getTechnicianId()));
        }

        MaintenanceOrder order = MaintenanceOrder.builder()
                .orderNumber("MO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .asset(asset)
                .technician(technician)
                .status(OrderStatus.PENDING)
                .triggerReason(request.getTriggerReason() != null ? request.getTriggerReason() : TriggerReason.MANUAL)
                .description(request.getDescription())
                .scheduledDate(request.getScheduledDate() != null ? request.getScheduledDate() : LocalDate.now().plusDays(7))
                .notes(request.getNotes())
                .build();

        asset.setStatus(AssetStatus.UNDER_MAINTENANCE);
        assetRepository.save(asset);

        return MaintenanceOrderResponse.from(maintenanceOrderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public MaintenanceOrderResponse getById(Long id) {
        return MaintenanceOrderResponse.from(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<MaintenanceOrderResponse> getAll() {
        return maintenanceOrderRepository.findAll().stream()
                .map(MaintenanceOrderResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<MaintenanceOrderResponse> getByStatus(OrderStatus status) {
        return maintenanceOrderRepository.findByStatus(status).stream()
                .map(MaintenanceOrderResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<MaintenanceOrderResponse> getByAssetId(Long assetId) {
        return maintenanceOrderRepository.findByAssetId(assetId).stream()
                .map(MaintenanceOrderResponse::from).toList();
    }

    public MaintenanceOrderResponse updateStatus(Long id, UpdateOrderStatusRequest request) {
        MaintenanceOrder order = getOrThrow(id);

        if (request.getTechnicianId() != null) {
            Technician technician = technicianRepository.findById(request.getTechnicianId())
                    .orElseThrow(() -> new ResourceNotFoundException("Technician not found: " + request.getTechnicianId()));
            order.setTechnician(technician);
        }
        if (request.getNotes() != null) {
            order.setNotes(request.getNotes());
        }

        order.setStatus(request.getStatus());

        switch (request.getStatus()) {
            case COMPLETED -> {
                order.setCompletedDate(LocalDate.now());
                order.getAsset().setStatus(AssetStatus.OPERATIONAL);
                order.getAsset().setLastMaintenanceDate(LocalDate.now());
                assetRepository.save(order.getAsset());
            }
            case CANCELLED -> {
                order.getAsset().setStatus(AssetStatus.OPERATIONAL);
                assetRepository.save(order.getAsset());
            }
            default -> { }
        }

        return MaintenanceOrderResponse.from(maintenanceOrderRepository.save(order));
    }

    public void delete(Long id) {
        maintenanceOrderRepository.delete(getOrThrow(id));
    }

    private MaintenanceOrder getOrThrow(Long id) {
        return maintenanceOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance order not found: " + id));
    }
}
