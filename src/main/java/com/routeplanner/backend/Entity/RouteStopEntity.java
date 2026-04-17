package com.routeplanner.backend.Entity;

import com.routeplanner.backend.Enums.RouteStopStatusEnum;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

    public RoutePlanEntity getRoutePlan() {
        return routePlan;
    }

    public void setRoutePlan(RoutePlanEntity routePlan) {
        this.routePlan = routePlan;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getRawAddress() {
        return rawAddress;
    }

    public void setRawAddress(String rawAddress) {
        this.rawAddress = rawAddress;
    }

    public String getNormalizedAddress() {
        return normalizedAddress;
    }

    public void setNormalizedAddress(String normalizedAddress) {
        this.normalizedAddress = normalizedAddress;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Integer getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(Integer sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public Integer getPreviousSequenceNo() {
        return previousSequenceNo;
    }

    public void setPreviousSequenceNo(Integer previousSequenceNo) {
        this.previousSequenceNo = previousSequenceNo;
    }

    public Integer getOptimizationRound() {
        return optimizationRound;
    }

    public void setOptimizationRound(Integer optimizationRound) {
        this.optimizationRound = optimizationRound;
    }

    public Integer getPriorityNo() {
        return priorityNo;
    }

    public void setPriorityNo(Integer priorityNo) {
        this.priorityNo = priorityNo;
    }

    public String getDeliveryNote() {
        return deliveryNote;
    }

    public void setDeliveryNote(String deliveryNote) {
        this.deliveryNote = deliveryNote;
    }

    public Timestamp getEstimatedArrivalTime() {
        return estimatedArrivalTime;
    }

    public void setEstimatedArrivalTime(Timestamp estimatedArrivalTime) {
        this.estimatedArrivalTime = estimatedArrivalTime;
    }

    public Timestamp getActualArrivalTime() {
        return actualArrivalTime;
    }

    public void setActualArrivalTime(Timestamp actualArrivalTime) {
        this.actualArrivalTime = actualArrivalTime;
    }

    public Timestamp getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Timestamp deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public RouteStopStatusEnum getStopStatus() {
        return stopStatus;
    }

    public void setStopStatus(RouteStopStatusEnum stopStatus) {
        this.stopStatus = stopStatus;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean locked) {
        isLocked = locked;
    }

    public Boolean getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(Boolean cancelled) {
        isCancelled = cancelled;
    }

    public String getNavigationUrl() {
        return navigationUrl;
    }

    public void setNavigationUrl(String navigationUrl) {
        this.navigationUrl = navigationUrl;
    }

    public Timestamp getLastNavigationOpenedAt() {
        return lastNavigationOpenedAt;
    }

    public void setLastNavigationOpenedAt(Timestamp lastNavigationOpenedAt) {
        this.lastNavigationOpenedAt = lastNavigationOpenedAt;
    }
}
