package com.servicecenter.service_center_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Request to create a work order")
public class CreateWorkOrderRequest {

    @Schema(description = "Appointment ID (optional, to link work order with an appointment)", example = "1")
    private Long appointmentId;

    @Schema(description = "Vehicle ID", example = "1", required = true)
    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @Schema(description = "Work order type", example = "PROJECT", required = true, allowableValues = {"SERVICE", "PROJECT"})
    @NotBlank(message = "Work order type is required")
    private String type;

    @Schema(description = "Work order title", example = "Custom Body Kit Installation", required = true)
    @NotBlank(message = "Title is required")
    private String title;

    @Schema(description = "Detailed description of the work required", example = "Install custom body kit with front and rear bumpers, side skirts, and rear spoiler")
    private String description;

    @Schema(description = "Estimated cost (optional)", example = "2500.00")
    private BigDecimal estimatedCost;

    @Schema(description = "Estimated completion date (optional)", example = "2025-10-30T15:00:00")
    private LocalDateTime estimatedCompletion;

    public CreateWorkOrderRequest() {
    }

    public CreateWorkOrderRequest(Long appointmentId, Long vehicleId, String type, String title, String description, 
                                 BigDecimal estimatedCost, LocalDateTime estimatedCompletion) {
        this.appointmentId = appointmentId;
        this.vehicleId = vehicleId;
        this.type = type;
        this.title = title;
        this.description = description;
        this.estimatedCost = estimatedCost;
        this.estimatedCompletion = estimatedCompletion;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(BigDecimal estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public LocalDateTime getEstimatedCompletion() {
        return estimatedCompletion;
    }

    public void setEstimatedCompletion(LocalDateTime estimatedCompletion) {
        this.estimatedCompletion = estimatedCompletion;
    }
}
