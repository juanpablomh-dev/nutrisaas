package com.nutrisaas.core.dto;

import lombok.Data;

@Data
public class UserResponse {
    private String email;
    private String fullName;
    private Boolean enabled;
}