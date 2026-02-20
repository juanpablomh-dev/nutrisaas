package com.nutrisaas.definitions.tenant.controller;

import com.nutrisaas.core.security.TokenProvider;
import com.nutrisaas.core.security.tenant.TenantContext;
import com.nutrisaas.definitions.tenant.model.Patient;
import com.nutrisaas.definitions.tenant.service.IPatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tenant/patients")
@RequiredArgsConstructor
public class PatientController {

    private final IPatientService patientService;
    private final TokenProvider tokenProvider;

    @GetMapping
    public ResponseEntity<List<Patient>> findByTenant() {
        return ResponseEntity.ok(patientService.getAllByTenant(TenantContext.getTenant()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getByIdAndTenant(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getByIdAndTenant(id, TenantContext.getTenant()));
    }

    @PostMapping
    public ResponseEntity<Patient> createByTenant(@RequestBody Patient data) {
        data.setId(null);
        Patient created = patientService.saveByTenant(data, TenantContext.getTenant());
        return ResponseEntity.created(URI.create("/api/tenant/patients")).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> updateByTenant(@PathVariable Long id, @RequestBody Patient data) {
        data.setId(id);
        Patient updated = patientService.saveByTenant(data, TenantContext.getTenant());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteByTenant(@PathVariable Long id) {
        patientService.deleteByTenant(id, TenantContext.getTenant());
        return ResponseEntity.noContent().build();
    }
}
