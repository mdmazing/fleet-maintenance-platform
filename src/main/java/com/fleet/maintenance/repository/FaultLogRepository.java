package com.fleet.maintenance.repository;

import com.fleet.maintenance.domain.FaultLog;
import com.fleet.maintenance.domain.FaultSeverity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaultLogRepository extends JpaRepository<FaultLog, Long> {

    List<FaultLog> findByAssetId(Long assetId);

    List<FaultLog> findByResolvedFalse();

    List<FaultLog> findBySeverity(FaultSeverity severity);

    List<FaultLog> findByAssetIdAndResolvedFalse(Long assetId);
}
