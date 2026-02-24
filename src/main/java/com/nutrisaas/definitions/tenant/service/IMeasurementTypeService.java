package com.nutrisaas.definitions.tenant.service;

import com.nutrisaas.definitions.tenant.dto.MeasurementTypeDTO;
import com.nutrisaas.definitions.tenant.model.MeasurementType;

import java.util.List;

public interface IMeasurementTypeService {
    List<MeasurementTypeDTO> findByTenantId(String tenant);

    MeasurementTypeDTO findByIdAndTenant(Long id, String tenant);

    MeasurementTypeDTO saveByTenant(MeasurementType measurementType, String tenant);

    void deleteByTenant(Long id, String tenant);
}
