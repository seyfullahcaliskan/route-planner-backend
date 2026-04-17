package com.routeplanner.backend.DTO.Request;

public class ReoptimizeRouteRequest {

    private Boolean includeSkippedStops;
    private Boolean includeFailedStops;
    private Boolean includePostponedStops;
    private String note;

    public Boolean getIncludeSkippedStops() {
        return includeSkippedStops;
    }

    public void setIncludeSkippedStops(Boolean includeSkippedStops) {
        this.includeSkippedStops = includeSkippedStops;
    }

    public Boolean getIncludeFailedStops() {
        return includeFailedStops;
    }

    public void setIncludeFailedStops(Boolean includeFailedStops) {
        this.includeFailedStops = includeFailedStops;
    }

    public Boolean getIncludePostponedStops() {
        return includePostponedStops;
    }

    public void setIncludePostponedStops(Boolean includePostponedStops) {
        this.includePostponedStops = includePostponedStops;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
