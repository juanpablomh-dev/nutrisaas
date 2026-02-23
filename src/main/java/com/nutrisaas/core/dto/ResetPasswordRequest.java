package com.nutrisaas.core.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Reset password request")
public class ResetPasswordRequest {

    @Schema(description = "Password reset token", example = "a8f9sd8f9sdf8sdf")
    @NotBlank
    private String token;

    @Schema(description = "New password", example = "NewStrongPassword123")
    @NotBlank
    private String newPassword;
}