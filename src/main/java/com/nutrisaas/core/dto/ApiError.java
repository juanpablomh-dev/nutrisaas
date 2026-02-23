package com.nutrisaas.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;

@Schema(description = "Standard API error response")
@Data
public class ApiError {

    @Schema(description = "Timestamp when the error occurred")
    private Instant timestamp;

    @Schema(description = "HTTP status code")
    private int status;

    @Schema(description = "Error type")
    private String error;

    @Schema(description = "Detailed error message")
    private String message;

}
