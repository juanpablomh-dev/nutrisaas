package com.nutrisaas.definitions.tenant.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EvolutionPointDTO {
    private LocalDateTime date;
    private String metric; // symbol
    private Double value;
    private String unit;
}
