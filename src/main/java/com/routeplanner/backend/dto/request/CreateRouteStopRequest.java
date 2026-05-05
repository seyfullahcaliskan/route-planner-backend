package com.routeplanner.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public class CreateRouteStopRequest {

    private String externalReference;
    private String customerName;
    private String customerPhone;

    @NotBlank
    private String rawAddress;

    private Integer priorityNo;
    private String deliveryNote;

    /**
     * Mobile harita üzerinden seçildiğinde lat/lng zaten elimizde — geocoding API çağrısına gerek yok.
     * Bu alanlar dolu gelirse backend reverse geocoding YAPMAZ; doğrudan kullanır.
     */
    private BigDecimal latitude;
    private BigDecimal longitude;

    public String getExternalReference() { return externalReference; }
    public void setExternalReference(String externalReference) { this.externalReference = externalReference; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getRawAddress() { return rawAddress; }
    public void setRawAddress(String rawAddress) { this.rawAddress = rawAddress; }

    public Integer getPriorityNo() { return priorityNo; }
    public void setPriorityNo(Integer priorityNo) { this.priorityNo = priorityNo; }

    public String getDeliveryNote() { return deliveryNote; }
    public void setDeliveryNote(String deliveryNote) { this.deliveryNote = deliveryNote; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }
}