package com.nutrisaas.definitions.tenant.dto;

import lombok.Data;

@Data
public class KPIDTO {
    private String symbol;
    private String name;
    private Double value;
    private String unit;
    private Double prevValue;    // nullable
}
