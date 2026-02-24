package com.nutrisaas.definitions.tenant.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String gender;
    private Boolean active;
    private String notes;

}
