package com.nutrisaas.definitions.tenant.service;

import com.nutrisaas.definitions.tenant.dto.PatientDTO;
import com.nutrisaas.definitions.tenant.model.Patient;

import java.util.List;

public interface IPatientService {
    List<PatientDTO> getAllByTenant(String tenant);

    PatientDTO getByIdAndTenant(Long id, String tenant);

    PatientDTO saveByTenant(Patient patient, String tenant);

    void deleteByTenant(Long id, String tenant);
}
