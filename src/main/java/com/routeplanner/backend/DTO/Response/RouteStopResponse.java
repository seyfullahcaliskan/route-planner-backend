package com.routeplanner.backend.DTO.Response;

import com.routeplanner.backend.Enums.RouteStopStatusEnum;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

public class RouteStopResponse {

    private UUID id;
    private String externalReference;
    private String customerName;
    private String customerPhone;
    private String rawAddress;
    private String normalizedAddress;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer sequenceNo;
    private Integer previousSequenceNo;
    private Integer optimizationRound;
    private Integer priorityNo;
    private String deliveryNote;
    private Timestamp estimatedArrivalTime;
    private Timestamp actualArrivalTime;
    private Timestamp deliveredAt;
    private RouteStopStatusEnum stopStatus;
    private Boolean isLocked;
    private Boolean isCancelled;
    private String navigationUrl;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
}