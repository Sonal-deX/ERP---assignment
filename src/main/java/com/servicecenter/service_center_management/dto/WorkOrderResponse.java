package com.servicecenter.service_center_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Work order details response")
public class WorkOrderResponse {

    @Schema(description = "Work order ID", example = "1")
    private Long id;

    @Schema(description = "Appointment ID (if linked)", example = "1")
    private Long appointmentId;

    @Schema(description = "Vehicle ID", example = "1")
    private Long vehicleId;

    @Schema(description = "Vehicle details", example = "Toyota Camry (ABC123)")
    private String vehicleDetails;

    @Schema(description = "Customer ID", example = "1")
    private Long customerId;

    @Schema(description = "Customer name", example = "John Doe")
    private String customerName;

    @Schema(description = "Work order type", example = "SERVICE")
    private String type;

    @Schema(description = "Work order title", example = "Oil Change")
    private String title;

    @Schema(description = "Work order description", example = "Regular oil change and filter replacement")
    private String description;

    @Schema(description = "Assigned employee ID", example = "2")
    private Long assignedEmployeeId;

    @Schema(description = "Assigned employee name", example = "Jane Smith")
    private String assignedEmployeeName;

    @Schema(description = "Work order status", example = "IN_PROGRESS")
    private String status;

    @Schema(description = "Progress percentage", example = "50")
    private int progressPercentage;

    @Schema(description = "Status message", example = "Working on oil change")
    private String statusMessage;

    @Schema(description = "Estimated cost", example = "150.00")
    private BigDecimal estimatedCost;

    @Schema(description = "Actual cost", example = "145.00")
    private BigDecimal actualCost;

    @Schema(description = "Estimated completion date", example = "2025-10-22T15:00:00")
    private LocalDateTime estimatedCompletion;

    @Schema(description = "Actual completion date", example = "2025-10-22T14:30:00")
    private LocalDateTime actualCompletion;

    @Schema(description = "Creation timestamp", example = "2025-10-20T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2025-10-20T12:00:00")
    private LocalDateTime updatedAt;

    public WorkOrderResponse() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getVehicleDetails() {
        return vehicleDetails;
    }

    public void setVehicleDetails(String vehicleDetails) {
        this.vehicleDetails = vehicleDetails;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public Long getAssignedEmployeeId() {
        return assignedEmployeeId;
    }

    public void setAssignedEmployeeId(Long assignedEmployeeId) {
        this.assignedEmployeeId = assignedEmployeeId;
    }

    public String getAssignedEmployeeName() {
        return assignedEmployeeName;
    }

    public void setAssignedEmployeeName(String assignedEmployeeName) {
        this.assignedEmployeeName = assignedEmployeeName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(BigDecimal estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public BigDecimal getActualCost() {
        return actualCost;
    }

    public void setActualCost(BigDecimal actualCost) {
        this.actualCost = actualCost;
    }

    public LocalDateTime getEstimatedCompletion() {
        return estimatedCompletion;
    }

    public void setEstimatedCompletion(LocalDateTime estimatedCompletion) {
        this.estimatedCompletion = estimatedCompletion;
    }

    public LocalDateTime getActualCompletion() {
        return actualCompletion;
    }

    public void setActualCompletion(LocalDateTime actualCompletion) {
        this.actualCompletion = actualCompletion;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
