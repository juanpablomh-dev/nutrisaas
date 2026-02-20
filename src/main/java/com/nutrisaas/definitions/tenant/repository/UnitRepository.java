package com.nutrisaas.definitions.tenant.repository;

import com.nutrisaas.definitions.tenant.model.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UnitRepository extends JpaRepository<Unit, Long> {

    List<Unit> findByTenant(String tenant);

    List<Unit> findByTenantOrderByNameAsc(String tenant);

    Optional<Unit> findByIdAndTenant(Long id, String tenant);

    Optional<Unit> findBySymbolAndTenant(String symbol, String tenant);

    boolean existsByTenantAndName(String tenant, String name);

    boolean existsByTenantAndSymbol(String tenant, String symbol);

    boolean existsBySymbol(String symbol);

    boolean existsBySymbolAndIdNotAndTenant(String symbol, Long id, String tenant);

    boolean existsBySymbolAndTenant(String symbol, String tenant);


}
