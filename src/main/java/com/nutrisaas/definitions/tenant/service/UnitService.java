package com.nutrisaas.definitions.tenant.service;

import com.nutrisaas.core.exception.ApiConflictException;
import com.nutrisaas.core.exception.ApiNotFoundException;
import com.nutrisaas.definitions.tenant.dto.UnitDTO;
import com.nutrisaas.definitions.tenant.mapper.UnitMapper;
import com.nutrisaas.definitions.tenant.model.Unit;
import com.nutrisaas.definitions.tenant.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Primary
@Service
@RequiredArgsConstructor
public class UnitService implements IUnitService {

    private final UnitRepository unitRepository;
    private final UnitMapper unitMapper;

    @Override
    public List<UnitDTO> findByTenant(String tenant) {
        return unitMapper.toDTOList(unitRepository.findByTenantOrderByNameAsc(tenant));
    }

    @Override
    public UnitDTO getByIdAndTenant(Long id, String tenant) {
        Unit unit = unitRepository.findByIdAndTenant(id, tenant)
                .orElseThrow(() -> new ApiNotFoundException("Unidad con id " + id + " no encontrada"));
        return unitMapper.toDTO(unit);
    }

    @Override
    public UnitDTO getBySymbolAndTenant(String symbol, String tenant) {
        Unit unit = unitRepository.findBySymbolAndTenant(symbol, tenant)
                .orElseThrow(() -> new ApiNotFoundException("Unidad con símbolo " + symbol + " no encontrada"));
        return unitMapper.toDTO(unit);
    }

    @Override
    public UnitDTO saveByTenant(Unit unit, String tenant) {
        return executeSafely(() -> {
            if (unit.getId() == null) {
                return unitMapper.toDTO(createUnitForTenant(unit, tenant));
            } else {
                return unitMapper.toDTO(updateUnitForTenant(unit, tenant));
            }
        });
    }

    @Override
    public void deleteByTenant(Long id, String tenant) {
        Unit unit = unitRepository.findByIdAndTenant(id, tenant)
                .orElseThrow(() -> new ApiNotFoundException("Unidad con id " + id + " no encontrada"));
        unitRepository.delete(unit);
    }

    // --- Métodos auxiliares ---

    private Unit createUnitForTenant(Unit unit, String tenant) {
        unit.setTenant(tenant);
        validateUniqueSymbol(unit);
        return unitRepository.save(unit);
    }

    private Unit updateUnitForTenant(Unit unit, String tenant) {
        Unit unitDB = unitRepository.findByIdAndTenant(unit.getId(), tenant)
                .orElseThrow(() -> new ApiNotFoundException("Unidad con id " + unit.getId() + " no encontrada"));
        validateUniqueSymbol(unit);
        unitDB.loadFromEntityToUpdate(unit);
        return unitRepository.save(unitDB);
    }

    private void validateUniqueSymbol(Unit unit) {
        boolean exists;
        if (unit.getId() == null) {
            exists = unitRepository.existsBySymbolAndTenant(unit.getSymbol(), unit.getTenant());
        } else {
            exists = unitRepository.existsBySymbolAndIdNotAndTenant(unit.getSymbol(), unit.getId(), unit.getTenant());
        }

        if (exists) {
            throw new ApiConflictException("El símbolo ya está en uso: " + unit.getSymbol());
        }
    }

    private <T> T executeSafely(SupplierWithException<T> action) {
        try {
            return action.get();
        } catch (DataIntegrityViolationException e) {
            throw new ApiConflictException("Error de integridad en la entidad Unidad", e);
        }
    }

    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get();
    }
}
