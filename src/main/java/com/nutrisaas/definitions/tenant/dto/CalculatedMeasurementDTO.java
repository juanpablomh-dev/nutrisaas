package com.nutrisaas.definitions.tenant.dto;

import lombok.Data;

@Data
public class CalculatedMeasurementDTO {
    private String id;
    private String symbol;
    private String name;
    private String description;
    private String formula;
    private UnitDTO resultUnit;
    private Boolean active;
    private String displayName;

}
