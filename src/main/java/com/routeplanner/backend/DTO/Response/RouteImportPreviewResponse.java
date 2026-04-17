package com.routeplanner.backend.DTO.Response;

import java.util.List;

public class RouteImportPreviewResponse {

    private Integer totalCount;
    private Integer validCount;
    private Integer invalidCount;
    private List<RouteImportPreviewItemResponse> items;

    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }

    public Integer getValidCount() { return validCount; }
    public void setValidCount(Integer validCount) { this.validCount = validCount; }

    public Integer getInvalidCount() { return invalidCount; }
    public void setInvalidCount(Integer invalidCount) { this.invalidCount = invalidCount; }

    public List<RouteImportPreviewItemResponse> getItems() { return items; }
    public void setItems(List<RouteImportPreviewItemResponse> items) { this.items = items; }
}
