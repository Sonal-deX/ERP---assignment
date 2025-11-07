package com.servicecenter.service_center_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to update part request status")
public class UpdatePartRequestStatusRequest {

    @NotBlank
    @Schema(description = "New status", example = "approved")
    private String status;

    public UpdatePartRequestStatusRequest() {}

    public UpdatePartRequestStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

