package com.nutrisaas.definitions.tenant.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AppointmentDTO {

    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String notes;
    private String status;
    private int type;
    private PatientDTO patient;
    private List<MeasurementDTO> measurements;
}
