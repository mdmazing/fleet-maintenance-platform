package com.fleet.maintenance.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "threshold_configurations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ThresholdConfiguration {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_type", nullable = false, unique = true, length = 50)
    private AssetType assetType;

    // Trigger maintenance when operating hours exceed this value
    @Column(name = "max_operating_hours", nullable = false)
    private double maxOperatingHours;

    // Trigger maintenance if nextMaintenanceDue has passed by more than this many days
    @Column(name = "overdue_threshold_days", nullable = false)
    private int overdueThresholdDays;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
