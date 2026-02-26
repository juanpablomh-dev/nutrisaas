package com.nutrisaas.definitions.tenant.service;

import com.nutrisaas.definitions.tenant.dto.UnitDTO;
import com.nutrisaas.definitions.tenant.model.Unit;

import java.util.List;

public interface IUnitService {
    List<UnitDTO> findByTenant(String tenant);

    UnitDTO getByIdAndTenant(Long id, String tenant);

    UnitDTO getBySymbolAndTenant(String symbol, String tenant);

    UnitDTO saveByTenant(Unit unit, String tenant);

    void deleteByTenant(Long id, String tenant);
}
