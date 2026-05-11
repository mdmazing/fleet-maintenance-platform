package com.fleet.maintenance.repository;

import com.fleet.maintenance.domain.Asset;
import com.fleet.maintenance.domain.AssetStatus;
import com.fleet.maintenance.domain.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findBySerialNumber(String serialNumber);

    boolean existsBySerialNumber(String serialNumber);

    List<Asset> findByStatus(AssetStatus status);

    List<Asset> findByType(AssetType type);

    List<Asset> findByNextMaintenanceDueBeforeAndStatus(LocalDate date, AssetStatus status);
}
