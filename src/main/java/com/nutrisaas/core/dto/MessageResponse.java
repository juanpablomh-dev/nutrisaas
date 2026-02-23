package com.nutrisaas.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Simple message response")
public class MessageResponse {

    @Schema(example = "If the account exists, you will receive an email with instructions")
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
