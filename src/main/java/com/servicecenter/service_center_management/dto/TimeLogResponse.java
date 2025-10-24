package com.servicecenter.service_center_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Time log details")
public class TimeLogResponse {

    @Schema(description = "Time log ID", example = "1")
    private Long id;

    @Schema(description = "Work order ID", example = "1")
    private Long workOrderId;

    @Schema(description = "Work order description", example = "Oil change service")
    private String workOrderDescription;

    @Schema(description = "Start time", example = "2025-10-20T09:00:00")
    private LocalDateTime startTime;

    @Schema(description = "End time", example = "2025-10-20T12:00:00")
    private LocalDateTime endTime;

    @Schema(description = "Duration in minutes", example = "180")
    private Long durationMinutes;

    @Schema(description = "Notes about the work performed", example = "Completed oil change and filter replacement")
    private String notes;

    @Schema(description = "Timestamp when log was created", example = "2025-10-20T12:05:00")
    private LocalDateTime loggedAt;

    public TimeLogResponse() {
    }

    public TimeLogResponse(Long id, Long workOrderId, String workOrderDescription, LocalDateTime startTime, 
                          LocalDateTime endTime, Long durationMinutes, String notes, LocalDateTime loggedAt) {
        this.id = id;
        this.workOrderId = workOrderId;
        this.workOrderDescription = workOrderDescription;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
        this.notes = notes;
        this.loggedAt = loggedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String getWorkOrderDescription() {
        return workOrderDescription;
    }

    public void setWorkOrderDescription(String workOrderDescription) {
        this.workOrderDescription = workOrderDescription;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Long getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getLoggedAt() {
        return loggedAt;
    }

    public void setLoggedAt(LocalDateTime loggedAt) {
        this.loggedAt = loggedAt;
    }
}
