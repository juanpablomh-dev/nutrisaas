package com.nutrisaas.definitions.tenant.controller;

import com.nutrisaas.core.security.TokenProvider;
import com.nutrisaas.core.security.tenant.TenantContext;
import com.nutrisaas.definitions.tenant.model.MeasurementType;
import com.nutrisaas.definitions.tenant.service.IMeasurementTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tenant/measurement-types")
@RequiredArgsConstructor
public class MeasurementTypeController {

    private final IMeasurementTypeService measurementTypeService;
    private final TokenProvider tokenProvider;


    @GetMapping
    public ResponseEntity<List<MeasurementType>> findByTenant() {
        return ResponseEntity.ok(measurementTypeService.findByTenantId(TenantContext.getTenant()));
    }

    @PostMapping
    public ResponseEntity<MeasurementType> createByTenant(@RequestBody MeasurementType data) {
        data.setId(null);
        MeasurementType created = measurementTypeService.saveByTenant(data, TenantContext.getTenant());
        return ResponseEntity.created(URI.create("/api/tenant/measurement-types")).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MeasurementType> updateByTenant(@PathVariable Long id, @RequestBody MeasurementType data) {
        data.setId(id);
        MeasurementType updated = measurementTypeService.saveByTenant(data, TenantContext.getTenant());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteByTenant(@PathVariable Long id) {
        measurementTypeService.deleteByTenant(id, TenantContext.getTenant());
        return ResponseEntity.noContent().build();
    }
}
