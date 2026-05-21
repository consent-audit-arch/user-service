package com.tcc.user_service.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "usage_records")
public class UsageRecordJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "service_type", nullable = false, length = 50)
    private String serviceType;

    @Column(name = "usage_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal usageAmount;

    @Column(name = "usage_unit", nullable = false, length = 20)
    private String usageUnit;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    public BigDecimal getUsageAmount() { return usageAmount; }
    public void setUsageAmount(BigDecimal usageAmount) { this.usageAmount = usageAmount; }
    public String getUsageUnit() { return usageUnit; }
    public void setUsageUnit(String usageUnit) { this.usageUnit = usageUnit; }
    public Instant getRecordedAt() { return recordedAt; }
    public void setRecordedAt(Instant recordedAt) { this.recordedAt = recordedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
