package com.nutrisaas.definitions.tenant.controller;

import com.nutrisaas.core.security.TokenProvider;
import com.nutrisaas.core.security.tenant.TenantContext;
import com.nutrisaas.definitions.tenant.dto.AppointmentDTO;
import com.nutrisaas.definitions.tenant.dto.AppointmentFilterDTO;
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
    public ResponseEntity<List<AppointmentDTO>> list(@RequestBody AppointmentFilterDTO appointmentFilterDTO) {
        return ResponseEntity.ok(appointmentService.findByListDto(TenantContext.getTenant(), appointmentFilterDTO));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDTO>> findByTenant() {
        return ResponseEntity.ok(appointmentService.findByTenant(TenantContext.getTenant()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> findByIdAndTenant(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.findByIdAndTenant(id, TenantContext.getTenant()));
    }

    @GetMapping("/range")
    public ResponseEntity<List<AppointmentDTO>> getByDateRange(
            @RequestParam("start") LocalDateTime start,
            @RequestParam("end") LocalDateTime end) {
        return ResponseEntity.ok(appointmentService.findByTenantAndStartTimeBetween(TenantContext.getTenant(), start, end));
    }

    @PostMapping
    public ResponseEntity<AppointmentDTO> createByTenant(@RequestBody Appointment appointment) {
        appointment.setId(null);
        AppointmentDTO created = appointmentService.saveByTenant(appointment, TenantContext.getTenant());
        return ResponseEntity.created(URI.create("/api/tenant/appointments")).body(created);
    }

    @PutMapping()
    public ResponseEntity<AppointmentDTO> updateByTenant(@RequestBody Appointment appointment) {
        AppointmentDTO updated = appointmentService.saveByTenant(appointment, TenantContext.getTenant());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteByTenant(@PathVariable Long id, HttpServletRequest request) {
        appointmentService.deleteByTenant(id, TenantContext.getTenant());
        return ResponseEntity.noContent().build();
    }
}
