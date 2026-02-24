package com.nutrisaas.definitions.tenant.service;

import com.nutrisaas.definitions.tenant.dto.MeasurementDTO;
import com.nutrisaas.definitions.tenant.model.Measurement;

import java.util.List;

public interface IMeasurementService {
    List<MeasurementDTO> findByTenantAndPatientId(String tenant, Long patientId);

    List<MeasurementDTO> findByTenantAndAppointment(String tenant, Long appointmentId);

    MeasurementDTO getByIdAndTenant(String id, String tenant);

    MeasurementDTO saveByTenant(Measurement measurement, String tenant);

    void deleteByIdAndTenant(String id, String tenant);
}
