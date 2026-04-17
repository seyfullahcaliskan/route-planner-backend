package com.routeplanner.backend.DTO.Response;

public class RouteImportPreviewItemResponse {

    private Integer rowNo;
    private String customerName;
    private String customerPhone;
    private String rawAddress;
    private Integer priorityNo;
    private Boolean valid;
    private String validationMessage;

    public Integer getRowNo() { return rowNo; }
    public void setRowNo(Integer rowNo) { this.rowNo = rowNo; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getRawAddress() { return rawAddress; }
    public void setRawAddress(String rawAddress) { this.rawAddress = rawAddress; }

    public Integer getPriorityNo() { return priorityNo; }
    public void setPriorityNo(Integer priorityNo) { this.priorityNo = priorityNo; }

    public Boolean getValid() { return valid; }
    public void setValid(Boolean valid) { this.valid = valid; }

    public String getValidationMessage() { return validationMessage; }
    public void setValidationMessage(String validationMessage) { this.validationMessage = validationMessage; }
}
