package com.nutrisaas.definitions.tenant.service;

import com.nutrisaas.definitions.tenant.model.MeasurementType;

import java.util.List;

public interface IMeasurementTypeService {
    List<MeasurementType> findByTenantId(String tenant);

    MeasurementType findByIdAndTenant(Long id, String tenant);

    MeasurementType saveByTenant(MeasurementType measurementType, String tenant);

    void deleteByTenant(Long id, String tenant);
}
