package com.nutrisaas.definitions.tenant.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class PatientHistoricalDTO {

    private LocalDateTime date;
    private Map<String, MeasurementDTO> measurements = new LinkedHashMap<>();
    private Map<String, MeasurementDTO> calculated = new LinkedHashMap<>();

    @Data
    public static class MeasurementDTO {
        private Double value;
        private String unit;
    }
}
