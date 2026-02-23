package com.nutrisaas.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Forgot password request")
public class ForgotPasswordRequest {

    @Email
    @NotBlank
    @Schema(description = "Registered email address", example = "user@example.com")
    private String email;
}