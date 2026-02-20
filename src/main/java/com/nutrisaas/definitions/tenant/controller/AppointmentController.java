package com.nutrisaas.definitions.tenant.controller;

import com.nutrisaas.core.security.TokenProvider;
import com.nutrisaas.core.security.tenant.TenantContext;
import com.nutrisaas.definitions.tenant.dto.AppointmentListDto;
import com.nutrisaas.definitions.tenant.model.Appointment;
import com.nutrisaas.definitions.tenant.service.IAppointmentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tenant/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final IAppointmentService appointmentService;
    private final TokenProvider tokenProvider;

    @PostMapping("/list")
    public ResponseEntity<List<Appointment>> list(@RequestBody AppointmentListDto appointmentListDto) {
        return ResponseEntity.ok(appointmentService.findByListDto(TenantContext.getTenant(), appointmentListDto));
    }

    @GetMapping
    public ResponseEntity<List<Appointment>> findByTenant() {
        return ResponseEntity.ok(appointmentService.findByTenant(TenantContext.getTenant()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> findByIdAndTenant(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.findByIdAndTenant(id, TenantContext.getTenant()));
    }

    @GetMapping("/range")
    public ResponseEntity<List<Appointment>> getByDateRange(
            @RequestParam("start") LocalDateTime start,
            @RequestParam("end") LocalDateTime end) {
        return ResponseEntity.ok(appointmentService.findByTenantAndStartTimeBetween(TenantContext.getTenant(), start, end));
    }

    @PostMapping
    public ResponseEntity<Appointment> createByTenant(@RequestBody Appointment appointment) {
        appointment.setId(null);
        Appointment created = appointmentService.saveByTenant(appointment, TenantContext.getTenant());
        return ResponseEntity.created(URI.create("/api/tenant/appointments")).body(created);
    }

    @PutMapping()
    public ResponseEntity<Appointment> updateByTenant(@RequestBody Appointment appointment) {
        Appointment updated = appointmentService.saveByTenant(appointment, TenantContext.getTenant());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteByTenant(@PathVariable Long id, HttpServletRequest request) {
        appointmentService.deleteByTenant(id, TenantContext.getTenant());
        return ResponseEntity.noContent().build();
    }
}
