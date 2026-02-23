package com.nutrisaas.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Login request payload")
public class AuthRequest {
    @Schema(description = "User email address", example = "user@example.com")
    private String email;
    @Schema(description = "User password in plain text", example = "StrongPassword123")
    private String password;
    @Schema(description = "Extend token expiration time", example = "true")
    private Boolean rememberMe;
}
