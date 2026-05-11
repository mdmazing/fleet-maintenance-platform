package com.fleet.maintenance.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "fault_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FaultLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(name = "reported_by", nullable = false, length = 150)
    private String reportedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FaultSeverity severity;

    @Column(nullable = false, length = 1000)
    private String description;

    @CreationTimestamp
    @Column(name = "reported_at", updatable = false)
    private LocalDateTime reportedAt;

    @Column(nullable = false)
    private boolean resolved = false;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    // Set when a maintenance order is auto-created for this fault
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_order_id")
    private MaintenanceOrder maintenanceOrder;
}
