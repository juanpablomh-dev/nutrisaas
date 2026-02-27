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
@RequestMapping("/api/v1/tenant/dashboard/patient/{patientId}")
@RequiredArgsConstructor
public class DashboardController {

    private final IDashboardService dashboardService;

    // SNAPSHOT
    @GetMapping("/snapshot")
    public ResponseEntity<PatientDashboardSnapshotDTO> getSnapshot(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "true") boolean latest
    ) {
        PatientDashboardSnapshotDTO dto =
                dashboardService.getSnapshot(
                        TenantContext.getTenant(),
                        patientId,
                        latest
                );

        return ResponseEntity.ok(dto);
    }

    // HISTORY
    @GetMapping("/history")
    public ResponseEntity<List<PatientHistoricalDTO>> getHistory(
            @PathVariable Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        validateRange(from, to);

        List<PatientHistoricalDTO> list =
                dashboardService.getHistorical(
                        TenantContext.getTenant(),
                        patientId,
                        from,
                        to
                );

        return ResponseEntity.ok(list);
    }

    // KPIS
    @GetMapping("/kpis")
    public ResponseEntity<List<KPIDTO>> getKpis(
            @PathVariable Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        validateRange(from, to);

        List<KPIDTO> list =
                dashboardService.getKPIs(
                        TenantContext.getTenant(),
                        patientId,
                        from,
                        to
                );

        return ResponseEntity.ok(list);
    }

    // EVOLUTION
    @GetMapping("/evolution")
    public ResponseEntity<List<EvolutionPointDTO>> getEvolution(
            @PathVariable Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) List<String> metrics
    ) {
        validateRange(from, to);

        List<String> requestedMetrics =
                (metrics == null || metrics.isEmpty())
                        ? List.of("WEIGHT")
                        : metrics;

        List<EvolutionPointDTO> list =
                dashboardService.getEvolution(
                        TenantContext.getTenant(),
                        patientId,
                        from,
                        to,
                        requestedMetrics
                );

        return ResponseEntity.ok(list);
    }

    // COMPARE
    @GetMapping("/compare")
    public ResponseEntity<List<ComparisonResultDTO>> comparePeriods(
            @PathVariable Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromA,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toA,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromB,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toB
    ) {
        validateRange(fromA, toA);
        validateRange(fromB, toB);

        List<ComparisonResultDTO> list =
                dashboardService.comparePeriods(
                        TenantContext.getTenant(),
                        patientId,
                        fromA,
                        toA,
                        fromB,
                        toB
                );

        return ResponseEntity.ok(list);
    }

    // METRICS
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, String>> getAvailableMetrics(
            @PathVariable Long patientId
    ) {
        Map<String, String> metrics =
                dashboardService.getAvailableMetrics(
                        TenantContext.getTenant()
                );

        return ResponseEntity.ok(metrics);
    }

    // VALIDATION
    private void validateRange(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Date range is required");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from must be before to");
        }
    }
}
