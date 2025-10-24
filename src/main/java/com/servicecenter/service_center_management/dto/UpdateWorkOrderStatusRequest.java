package com.servicecenter.service_center_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to update work order status")
public class UpdateWorkOrderStatusRequest {

    @Schema(description = "Work order status", example = "IN_PROGRESS", required = true, allowableValues = {"IN_PROGRESS", "COMPLETED"})
    @NotNull(message = "Status is required")
    private String status;

    public UpdateWorkOrderStatusRequest() {
    }

    public UpdateWorkOrderStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
