package com.nutrisaas.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserResponse {
    @Schema(description = "User email address", example = "user@example.com")
    private String email;
    @Schema(description = "User fullName ", example = "Juan Pablo Muñoz")
    private String fullName;
    @Schema(description = "Status (enabled yes or not)", example = "true")
    private Boolean enabled;
}