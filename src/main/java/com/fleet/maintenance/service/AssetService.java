package com.fleet.maintenance.service;

import com.fleet.maintenance.domain.Asset;
import com.fleet.maintenance.domain.AssetStatus;
import com.fleet.maintenance.dto.request.CreateAssetRequest;
import com.fleet.maintenance.dto.request.UpdateAssetRequest;
import com.fleet.maintenance.dto.response.AssetResponse;
import com.fleet.maintenance.exception.ConflictException;
import com.fleet.maintenance.exception.ResourceNotFoundException;
import com.fleet.maintenance.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssetService {

    private final AssetRepository assetRepository;

    public List<AssetResponse> getAll() {
        return assetRepository.findAll().stream().map(AssetResponse::from).toList();
    }

    public AssetResponse getById(Long id) {
        return AssetResponse.from(getOrThrow(id));
    }

    public AssetResponse getBySerialNumber(String serialNumber) {
        return AssetResponse.from(assetRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found: " + serialNumber)));
    }

    public List<AssetResponse> getByStatus(AssetStatus status) {
        return assetRepository.findByStatus(status).stream().map(AssetResponse::from).toList();
    }

    public List<AssetResponse> getOverdueAssets() {
        return assetRepository.findByNextMaintenanceDueBeforeAndStatus(LocalDate.now(), AssetStatus.OPERATIONAL)
                .stream().map(AssetResponse::from).toList();
    }

    @Transactional
    public AssetResponse create(CreateAssetRequest req) {
        if (assetRepository.existsBySerialNumber(req.getSerialNumber())) {
            throw new ConflictException("Serial number already exists: " + req.getSerialNumber());
        }
        Asset asset = Asset.builder()
                .serialNumber(req.getSerialNumber())
                .type(req.getType())
                .location(req.getLocation())
                .operatingHours(req.getOperatingHours())
                .status(AssetStatus.OPERATIONAL)
                .lastMaintenanceDate(req.getLastMaintenanceDate())
                .nextMaintenanceDue(req.getNextMaintenanceDue())
                .build();
        return AssetResponse.from(assetRepository.save(asset));
    }

    @Transactional
    public AssetResponse update(Long id, UpdateAssetRequest req) {
        Asset asset = getOrThrow(id);
        asset.setLocation(req.getLocation());
        if (req.getStatus() != null) {
            asset.setStatus(req.getStatus());
        }
        if (req.getOperatingHours() != null) {
            asset.setOperatingHours(req.getOperatingHours());
        }
        asset.setLastMaintenanceDate(req.getLastMaintenanceDate());
        asset.setNextMaintenanceDue(req.getNextMaintenanceDue());
        return AssetResponse.from(assetRepository.save(asset));
    }

    @Transactional
    public AssetResponse updateOperatingHours(Long id, double hours) {
        Asset asset = getOrThrow(id);
        asset.setOperatingHours(hours);
        return AssetResponse.from(assetRepository.save(asset));
    }

    @Transactional
    public void delete(Long id) {
        getOrThrow(id);
        assetRepository.deleteById(id);
    }

    Asset getOrThrow(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found: " + id));
    }
}
