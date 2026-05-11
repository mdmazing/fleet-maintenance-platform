package com.fleet.maintenance.service;

import com.fleet.maintenance.domain.Asset;
import com.fleet.maintenance.domain.FaultLog;
import com.fleet.maintenance.domain.FaultSeverity;
import com.fleet.maintenance.domain.MaintenanceOrder;
import com.fleet.maintenance.domain.TriggerReason;
import com.fleet.maintenance.dto.request.CreateFaultLogRequest;
import com.fleet.maintenance.dto.request.CreateMaintenanceOrderRequest;
import com.fleet.maintenance.dto.response.FaultLogResponse;
import com.fleet.maintenance.dto.response.MaintenanceOrderResponse;
import com.fleet.maintenance.exception.ResourceNotFoundException;
import com.fleet.maintenance.repository.AssetRepository;
import com.fleet.maintenance.repository.FaultLogRepository;
import com.fleet.maintenance.repository.MaintenanceOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FaultLogService {

    private final FaultLogRepository faultLogRepository;
    private final AssetRepository assetRepository;
    private final MaintenanceOrderRepository maintenanceOrderRepository;
    private final MaintenanceOrderService maintenanceOrderService;

    public FaultLogResponse create(CreateFaultLogRequest request) {
        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found: " + request.getAssetId()));

        FaultLog faultLog = FaultLog.builder()
                .asset(asset)
                .reportedBy(request.getReportedBy())
                .severity(request.getSeverity())
                .description(request.getDescription())
                .resolved(false)
                .build();
        faultLog = faultLogRepository.save(faultLog);

        // HIGH/CRITICAL faults automatically escalate to a maintenance order
        if (request.getSeverity() == FaultSeverity.HIGH || request.getSeverity() == FaultSeverity.CRITICAL) {
            MaintenanceOrderResponse orderResponse = maintenanceOrderService.create(
                    CreateMaintenanceOrderRequest.builder()
                            .assetId(asset.getId())
                            .triggerReason(TriggerReason.FAULT_REPORTED)
                            .description("Auto-triggered by " + request.getSeverity()
                                    + " fault: " + request.getDescription())
                            .scheduledDate(LocalDate.now().plusDays(1))
                            .build());

            // Link the fault log back to the order so the relationship is navigable both ways
            MaintenanceOrder order = maintenanceOrderRepository.findById(orderResponse.getId()).orElseThrow();
            faultLog.setMaintenanceOrder(order);
            faultLog = faultLogRepository.save(faultLog);
        }

        return FaultLogResponse.from(faultLog);
    }

    @Transactional(readOnly = true)
    public FaultLogResponse getById(Long id) {
        return FaultLogResponse.from(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<FaultLogResponse> getAll() {
        return faultLogRepository.findAll().stream().map(FaultLogResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<FaultLogResponse> getByAssetId(Long assetId) {
        return faultLogRepository.findByAssetId(assetId).stream().map(FaultLogResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<FaultLogResponse> getUnresolved() {
        return faultLogRepository.findByResolvedFalse().stream().map(FaultLogResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<FaultLogResponse> getBySeverity(FaultSeverity severity) {
        return faultLogRepository.findBySeverity(severity).stream().map(FaultLogResponse::from).toList();
    }

    public FaultLogResponse resolve(Long id) {
        FaultLog faultLog = getOrThrow(id);
        faultLog.setResolved(true);
        faultLog.setResolvedAt(LocalDateTime.now());
        return FaultLogResponse.from(faultLogRepository.save(faultLog));
    }

    public void delete(Long id) {
        faultLogRepository.delete(getOrThrow(id));
    }

    private FaultLog getOrThrow(Long id) {
        return faultLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fault log not found: " + id));
    }
}
