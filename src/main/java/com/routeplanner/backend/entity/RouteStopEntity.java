package com.routeplanner.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.routeplanner.backend.enums.RouteStopStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "route_stop")
public class RouteStopEntity extends BaseEntity {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_plan_id", nullable = false)
    private RoutePlanEntity routePlan;

    @Column(name = "external_reference", length = 100)
    private String externalReference;

    @Column(name = "customer_name", length = 150)
    private String customerName;

    @Column(name = "customer_phone", length = 30)
    private String customerPhone;

    @Column(name = "raw_address", nullable = false, length = 1000)
    private String rawAddress;

    @Column(name = "normalized_address", length = 1000)
    private String normalizedAddress;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "sequence_no", nullable = false)
    private Integer sequenceNo;

    @Column(name = "previous_sequence_no")
    private Integer previousSequenceNo;

    @Column(name = "optimization_round", nullable = false)
    private Integer optimizationRound = 1;

    @Column(name = "priority_no", nullable = false)
    private Integer priorityNo = 0;

    @Column(name = "delivery_note", length = 1000)
    private String deliveryNote;

    @Column(name = "estimated_arrival_time")
    private Timestamp estimatedArrivalTime;

    @Column(name = "actual_arrival_time")
    private Timestamp actualArrivalTime;

    @Column(name = "delivered_at")
    private Timestamp deliveredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "stop_status", nullable = false, length = 30)
    private RouteStopStatusEnum stopStatus = RouteStopStatusEnum.PENDING;

    @Column(name = "is_locked", nullable = false)
    private Boolean isLocked = false;

    @Column(name = "is_cancelled", nullable = false)
    private Boolean isCancelled = false;

    @Column(name = "navigation_url", length = 1000)
    private String navigationUrl;

    @Column(name = "last_navigation_opened_at")
    private Timestamp lastNavigationOpenedAt;
}