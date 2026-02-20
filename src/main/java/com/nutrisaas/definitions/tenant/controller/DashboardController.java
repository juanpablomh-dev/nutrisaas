package com.nutrisaas.definitions.tenant.controller;

import com.nutrisaas.core.security.tenant.TenantContext;
import com.nutrisaas.definitions.tenant.dto.*;
import com.nutrisaas.definitions.tenant.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tenant/dashboard/patient")
@RequiredArgsConstructor
public class DashboardController {

    private final IDashboardService dashboardService;

    // snapshot: latest or previous
    @GetMapping("/{patientId}/snapshot")
    public Map<String, Object> snapshot(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "true") boolean latest
    ) {
        PatientDashboardSnapshotDTO dto = dashboardService.getSnapshot(TenantContext.getTenant(), patientId, latest);
        return Map.of("data", dto);
    }

    @GetMapping("/{patientId}/history")
    public ResponseEntity<List<PatientHistoricalDTO>> history(
            @PathVariable Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        List<PatientHistoricalDTO> list = dashboardService.getHistorical(TenantContext.getTenant(), patientId, from, to);
        return ResponseEntity.ok(list);
    }


    // kpis (latest + previous computed from history range)
    @GetMapping("/{patientId}/kpis")
    public Map<String, Object> kpis(
            @PathVariable Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        List<KPIDTO> res = dashboardService.getKPIs(TenantContext.getTenant(), patientId, from, to);
        return Map.of("data", res);
    }

    // evolution: receives metrics csv (symbols) as query param 'metrics' or multiple metrics
    @GetMapping("/{patientId}/evolution")
    public Map<String, Object> evolution(
            @PathVariable Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) List<String> metrics
    ) {
        List<EvolutionPointDTO> s = dashboardService.getEvolution(TenantContext.getTenant(), patientId, from, to, metrics == null ? List.of("WEIGHT") : metrics);
        return Map.of("data", s);
    }

    // compare two periods: provide fromA,toA,fromB,toB as ISO local datetime
    @GetMapping("/{patientId}/compare")
    public Map<String, Object> compare(
            @PathVariable Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromA,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toA,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromB,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toB
    ) {
        List<ComparisonResultDTO> res = dashboardService.comparePeriods(TenantContext.getTenant(), patientId, fromA, toA, fromB, toB);
        return Map.of("data", res);
    }

    @GetMapping("/{patientId}/metrics")
    public ResponseEntity<?> getMetrics(@PathVariable("patientId") Long patientId) {
        Map<String, String> metrics = dashboardService.getAvailableMetrics(TenantContext.getTenant());
        return ResponseEntity.ok(metrics);
    }
}
