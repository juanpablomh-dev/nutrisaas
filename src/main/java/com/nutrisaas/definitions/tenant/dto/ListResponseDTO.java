package com.nutrisaas.definitions.tenant.dto;

import java.util.List;

public record ListResponseDTO<T>(List<T> data) {
}