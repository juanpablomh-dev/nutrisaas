package com.nutrisaas.definitions.tenant.service;

import com.nutrisaas.definitions.tenant.model.Measurement;

import java.util.List;

public interface IMeasurementService {
    List<Measurement> findByTenantAndPatientId(String tenant, Long patientId);

    List<Measurement> findByTenantAndAppointment(String tenant, Long appointmentId);

    Measurement getByIdAndTenant(String id, String tenant);

    Measurement saveByTenant(Measurement measurement, String tenant);

    void deleteByIdAndTenant(String id, String tenant);
}
