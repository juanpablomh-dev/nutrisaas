package com.nutrisaas.definitions.tenant.controller;

import com.nutrisaas.core.security.TokenProvider;
import com.nutrisaas.core.security.tenant.TenantContext;
import com.nutrisaas.definitions.tenant.model.Measurement;
import com.nutrisaas.definitions.tenant.service.IMeasurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenant/measurements")
@RequiredArgsConstructor
public class MeasurementController {

    private final IMeasurementService measurementService;
    private final TokenProvider tokenProvider;

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Measurement>> findByTenantAndPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(measurementService.findByTenantAndPatientId(TenantContext.getTenant(), patientId));
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<List<Measurement>> findByTenantAndAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(measurementService.findByTenantAndAppointment(TenantContext.getTenant(), appointmentId));
    }

    @PostMapping
    public ResponseEntity<Measurement> saveByTenant(@RequestBody Measurement measurement) {
        return ResponseEntity.ok(measurementService.saveByTenant(measurement, TenantContext.getTenant()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteByIdAndTenant(@PathVariable String id) {
        measurementService.deleteByIdAndTenant(id, TenantContext.getTenant());
        return ResponseEntity.noContent().build();
    }
}
