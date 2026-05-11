package com.fleet.maintenance.repository;

import com.fleet.maintenance.domain.AssetType;
import com.fleet.maintenance.domain.ThresholdConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThresholdConfigurationRepository extends JpaRepository<ThresholdConfiguration, Long> {

    Optional<ThresholdConfiguration> findByAssetType(AssetType assetType);
}
