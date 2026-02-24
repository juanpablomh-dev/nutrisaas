package com.nutrisaas.definitions.tenant.dto;

import lombok.Data;

@Data
public class MeasurementTypeDTO {
    private String id;
    private String name;
    private String symbol;
    private UnitDTO defaultUnit;
    private Boolean active;
    private String displayName;
}

