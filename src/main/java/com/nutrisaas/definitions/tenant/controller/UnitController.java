package com.nutrisaas.definitions.tenant.controller;

import com.nutrisaas.core.security.TokenProvider;
import com.nutrisaas.core.security.tenant.TenantContext;
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
    public ResponseEntity<List<Unit>> findByTenant() {
        return ResponseEntity.ok(unitService.findByTenant(TenantContext.getTenant()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Unit> getByIdAndTenant(@PathVariable Long id) {
        return ResponseEntity.ok(unitService.getByIdAndTenant(id, TenantContext.getTenant()));
    }

    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<Unit> getBySymbolAndTenant(@PathVariable String symbol) {
        return ResponseEntity.ok(unitService.getBySymbolAndTenant(symbol, TenantContext.getTenant()));
    }

    @PostMapping
    public ResponseEntity<Unit> createByTenant(@RequestBody Unit data) {
        data.setId(null);
        Unit created = unitService.saveByTenant(data, TenantContext.getTenant());
        return ResponseEntity.created(URI.create("/api/tenant/units")).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Unit> updateByTenant(@PathVariable Long id, @RequestBody Unit data) {
        data.setId(id);
        Unit updated = unitService.saveByTenant(data, TenantContext.getTenant());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteByTenant(@PathVariable Long id) {
        unitService.deleteByTenant(id, TenantContext.getTenant());
        return ResponseEntity.noContent().build();
    }

}
