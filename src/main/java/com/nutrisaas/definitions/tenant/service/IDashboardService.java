package com.nutrisaas.definitions.tenant.service;

import com.nutrisaas.definitions.tenant.dto.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public interface IDashboardService {

    PatientDashboardSnapshotDTO getSnapshot(String tenant, Long patientId, boolean latest);

    List<PatientHistoricalDTO> getHistorical(String tenant, Long patientId, LocalDateTime from, LocalDateTime to);

    List<KPIDTO> getKPIs(String tenant, Long patientId, LocalDateTime from, LocalDateTime to);

    List<EvolutionPointDTO> getEvolution(String tenant, Long patientId, LocalDateTime from, LocalDateTime to, List<String> metrics);

    List<ComparisonResultDTO> comparePeriods(String tenant, Long patientId, LocalDateTime fromA, LocalDateTime toA, LocalDateTime fromB, LocalDateTime toB);

    Map<String, String> getAvailableMetrics(String tenant);
}
