package com.nutrisaas.definitions.tenant.service;

import com.nutrisaas.definitions.tenant.model.Unit;

import java.util.List;

public interface IUnitService {
    List<Unit> findByTenant(String tenant);

    Unit getByIdAndTenant(Long id, String tenant);

    Unit getBySymbolAndTenant(String symbol, String tenant);

    Unit saveByTenant(Unit unit, String tenant);

    void deleteByTenant(Long id, String tenant);
}
