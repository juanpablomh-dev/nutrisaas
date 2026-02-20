package com.nutrisaas.definitions.tenant.dto;

import lombok.Data;

@Data
public class ComparisonResultDTO {
    private String symbol;
    private String name;
    private Double periodA_avg;
    private Double periodB_avg;
    private Double delta;
    private String unit;
}
