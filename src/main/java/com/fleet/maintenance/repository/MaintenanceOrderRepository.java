package com.fleet.maintenance.repository;

import com.fleet.maintenance.domain.MaintenanceOrder;
import com.fleet.maintenance.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MaintenanceOrderRepository extends JpaRepository<MaintenanceOrder, Long> {

    List<MaintenanceOrder> findByAssetId(Long assetId);

    List<MaintenanceOrder> findByStatus(OrderStatus status);

    List<MaintenanceOrder> findByTechnicianId(Long technicianId);

    // Check if there is already an open order for this asset (avoids duplicates from the scheduler)
    @Query("""
        SELECT COUNT(o) > 0 FROM MaintenanceOrder o
        WHERE o.asset.id = :assetId
          AND o.status NOT IN ('COMPLETED', 'CANCELLED')
        """)
    boolean hasOpenOrderForAsset(Long assetId);
}
