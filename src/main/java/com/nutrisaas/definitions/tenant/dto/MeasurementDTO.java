package com.nutrisaas.definitions.tenant.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class MeasurementDTO {

    private String id;
    private Double value;
    private Instant measuredAt;

    private MeasurementTypeDTO measurementType;
    private UnitDTO unit;
}
