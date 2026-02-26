package com.nutrisaas.definitions.tenant.controller;

import com.nutrisaas.core.security.TokenProvider;
import com.nutrisaas.core.security.tenant.TenantContext;
import com.nutrisaas.definitions.tenant.dto.UnitDTO;
import com.nutrisaas.definitions.tenant.model.Unit;
import com.nutrisaas.definitions.tenant.service.IUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tenant/units")
@RequiredArgsConstructor
public class UnitController {

    private final IUnitService unitService;
    private final TokenProvider tokenProvider;

    @GetMapping
    public ResponseEntity<List<UnitDTO>> findByTenant() {
        return ResponseEntity.ok(unitService.findByTenant(TenantContext.getTenant()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnitDTO> getByIdAndTenant(@PathVariable Long id) {
        return ResponseEntity.ok(unitService.getByIdAndTenant(id, TenantContext.getTenant()));
    }

    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<UnitDTO> getBySymbolAndTenant(@PathVariable String symbol) {
        return ResponseEntity.ok(unitService.getBySymbolAndTenant(symbol, TenantContext.getTenant()));
    }

    @PostMapping
    public ResponseEntity<UnitDTO> createByTenant(@RequestBody Unit data) {
        data.setId(null);
        UnitDTO created = unitService.saveByTenant(data, TenantContext.getTenant());
        return ResponseEntity.created(URI.create("/api/tenant/units")).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnitDTO> updateByTenant(@PathVariable Long id, @RequestBody Unit data) {
        data.setId(id);
        UnitDTO updated = unitService.saveByTenant(data, TenantContext.getTenant());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteByTenant(@PathVariable Long id) {
        unitService.deleteByTenant(id, TenantContext.getTenant());
        return ResponseEntity.noContent().build();
    }

}
