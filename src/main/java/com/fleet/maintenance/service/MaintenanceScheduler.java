package com.fleet.maintenance.service;

import com.fleet.maintenance.domain.Asset;
import com.fleet.maintenance.domain.AssetStatus;
import com.fleet.maintenance.domain.TriggerReason;
import com.fleet.maintenance.domain.ThresholdConfiguration;
import com.fleet.maintenance.dto.request.CreateMaintenanceOrderRequest;
import com.fleet.maintenance.repository.AssetRepository;
import com.fleet.maintenance.repository.MaintenanceOrderRepository;
import com.fleet.maintenance.repository.ThresholdConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MaintenanceScheduler {

    private final AssetRepository assetRepository;
    private final ThresholdConfigurationRepository thresholdConfigRepository;
    private final MaintenanceOrderRepository maintenanceOrderRepository;
    private final MaintenanceOrderService maintenanceOrderService;

    @Scheduled(fixedDelayString = "${fleet.scheduler.check-interval-ms:300000}")
    @Transactional
    public void checkOverdueAssets() {
        List<Asset> overdue = assetRepository
                .findByNextMaintenanceDueBeforeAndStatus(LocalDate.now(), AssetStatus.OPERATIONAL);
        for (Asset asset : overdue) {
            if (maintenanceOrderRepository.hasOpenOrderForAsset(asset.getId())) {
                continue;
            }
            maintenanceOrderService.create(CreateMaintenanceOrderRequest.builder()
                    .assetId(asset.getId())
                    .triggerReason(TriggerReason.OVERDUE_SCHEDULE)
                    .description("Auto-triggered: asset overdue for maintenance (due "
                            + asset.getNextMaintenanceDue() + ")")
                    .scheduledDate(LocalDate.now().plusDays(1))
                    .build());
            log.info("Auto-created order for overdue asset {}", asset.getSerialNumber());
        }
    }

    @Scheduled(fixedDelayString = "${fleet.scheduler.check-interval-ms:300000}")
    @Transactional
    public void checkHoursThreshold() {
        List<Asset> operational = assetRepository.findByStatus(AssetStatus.OPERATIONAL);
        for (Asset asset : operational) {
            ThresholdConfiguration cfg = thresholdConfigRepository
                    .findByAssetType(asset.getType()).orElse(null);
            if (cfg == null || asset.getOperatingHours() < cfg.getMaxOperatingHours()) {
                continue;
            }
            if (maintenanceOrderRepository.hasOpenOrderForAsset(asset.getId())) {
                continue;
            }
            maintenanceOrderService.create(CreateMaintenanceOrderRequest.builder()
                    .assetId(asset.getId())
                    .triggerReason(TriggerReason.HOURS_EXCEEDED)
                    .description("Auto-triggered: operating hours " + asset.getOperatingHours()
                            + " exceeded threshold " + cfg.getMaxOperatingHours())
                    .scheduledDate(LocalDate.now().plusDays(1))
                    .build());
            log.info("Auto-created order for asset {} (hours threshold)", asset.getSerialNumber());
        }
    }
}
