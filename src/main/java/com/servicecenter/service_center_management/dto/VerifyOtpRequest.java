package com.servicecenter.service_center_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "OTP verification request")
public class VerifyOtpRequest {
    
    @NotBlank
    @Schema(description = "6-digit OTP code sent to email", example = "123456", required = true)
    private String otpCode;
    
    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }
}