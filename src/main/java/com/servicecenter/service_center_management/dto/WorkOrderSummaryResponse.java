package com.servicecenter.service_center_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Summary of work orders for today")
public class WorkOrderSummaryResponse {

    @Schema(description = "Total work orders estimated to complete today", example = "5")
    private int totalToday;

    @Schema(description = "Work orders in progress today", example = "3")
    private int inProgressToday;

    @Schema(description = "Work orders completed today", example = "2")
    private int completedToday;

    // Getters and Setters
    public int getTotalToday() {
        return totalToday;
    }

    public void setTotalToday(int totalToday) {
        this.totalToday = totalToday;
    }

    public int getInProgressToday() {
        return inProgressToday;
    }

    public void setInProgressToday(int inProgressToday) {
        this.inProgressToday = inProgressToday;
    }

    public int getCompletedToday() {
        return completedToday;
    }

    public void setCompletedToday(int completedToday) {
        this.completedToday = completedToday;
    }
}