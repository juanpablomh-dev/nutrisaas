package com.nutrisaas.definitions.tenant.service;

import com.nutrisaas.definitions.tenant.model.Patient;

import java.util.List;

public interface IPatientService {
    List<Patient> getAllByTenant(String tenant);

    Patient getByIdAndTenant(Long id, String tenant);

    Patient saveByTenant(Patient patient, String tenant);

    void deleteByTenant(Long id, String tenant);
}
