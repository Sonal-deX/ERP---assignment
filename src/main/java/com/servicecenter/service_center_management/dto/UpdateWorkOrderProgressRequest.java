package com.servicecenter.service_center_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to update work order progress")
public class UpdateWorkOrderProgressRequest {

    @Schema(description = "Progress percentage (0-100)", example = "75", required = true)
    @NotNull(message = "Progress percentage is required")
    @Min(value = 0, message = "Progress percentage must be at least 0")
    @Max(value = 100, message = "Progress percentage must be at most 100")
    private Integer progressPercentage;

    @Schema(description = "Status message", example = "Almost done with oil change")
    private String statusMessage;

    public UpdateWorkOrderProgressRequest() {
    }

    public UpdateWorkOrderProgressRequest(Integer progressPercentage, String statusMessage) {
        this.progressPercentage = progressPercentage;
        this.statusMessage = statusMessage;
    }

    public Integer getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Integer progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
