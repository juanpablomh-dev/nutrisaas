package com.nutrisaas.definitions.tenant.service;

import com.nutrisaas.definitions.tenant.dto.*;
import com.nutrisaas.definitions.tenant.model.Appointment;
import com.nutrisaas.definitions.tenant.model.CalculatedMeasurement;
import com.nutrisaas.definitions.tenant.model.Measurement;
import com.nutrisaas.definitions.tenant.model.MeasurementType;
import com.nutrisaas.definitions.tenant.repository.AppointmentRepository;
import com.nutrisaas.definitions.tenant.repository.CalculatedMeasurementRepository;
import com.nutrisaas.definitions.tenant.repository.MeasurementRepository;
import com.nutrisaas.definitions.tenant.repository.MeasurementTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService implements IDashboardService {

    private final MeasurementRepository measurementRepository;
    private final CalculatedMeasurementRepository calcRepo;
    private final AppointmentRepository appointmentRepository;
    private final ICalculatedMeasurementService formulaService;
    private final MeasurementTypeRepository measurementTypeRepository;

/*
    @Override
    public PatientDashboardSnapshotDTO getSnapshot(String tenant, Long patientId, boolean latest) {

        PatientDashboardSnapshotDTO dto = new PatientDashboardSnapshotDTO();

        // 1) obtener todas las citas del paciente ordenadas por fecha descendente
        List<Appointment> appointments = appointmentRepository.findByPatientIdAndTenantOrderByStartTimeDesc(patientId, tenant);
        if (appointments.isEmpty()) return dto;

        // 2) determinar la cita de referencia: última o anterior
        Appointment referenceAppointment = latest ? appointments.get(0)
                : appointments.size() > 1 ? appointments.get(1) : null;
        if (referenceAppointment == null) return dto;

        // 3) obtener todas las mediciones de esa cita
        List<Measurement> batch = measurementRepository.findByAppointmentIdAndTenant(referenceAppointment.getId(), tenant);
        if (batch.isEmpty()) return dto;

        // 4) llenar mediciones reales en el DTO
        for (Measurement m : batch) {
            String key = m.getMeasurementType().getSymbol();

            PatientDashboardSnapshotDTO.MeasurementDTO mm = new PatientDashboardSnapshotDTO.MeasurementDTO();
            mm.setName(m.getMeasurementType().getName());
            mm.setValue(m.getValue());
            mm.setUnit(m.getUnit() != null ? m.getUnit().getSymbol() : null);

            dto.getMeasurements().put(key, mm);
        }

        // 5) evaluar mediciones calculadas
        List<CalculatedMeasurement> calcList = calculatedMeasurementRepository.findByActiveTrueAndTenant(tenant);
        Map<String, Double> dataMap = batch.stream().collect(
                Collectors.toMap(
                        m -> m.getMeasurementType().getSymbol(),
                        Measurement::getValue
                )
        );

        for (CalculatedMeasurement c : calcList) {
            Optional<Double> result = iCalculatedMeasurementService.evaluateFormulaWithValues(c.getFormula(), dataMap);
            result.ifPresent(val -> {
                PatientDashboardSnapshotDTO.MeasurementDTO cm = new PatientDashboardSnapshotDTO.MeasurementDTO();
                cm.setName(c.getName());
                cm.setValue(val);
                cm.setUnit(c.getResultUnit().getSymbol());
                dto.getCalculated().put(c.getSymbol(), cm);
            });
        }

        return dto;
    }

    @Override
    public List<PatientHistoricalDTO> getHistorical(String tenant, Long patientId, LocalDateTime from, LocalDateTime to) {
        // 1) Obtener citas del paciente en el rango
        List<Appointment> appointments = appointmentRepository
                .findByPatientIdAndTenantOrderByStartTimeDesc(patientId, tenant)
                .stream()
                .filter(a -> !a.getStartTime().isBefore(from) && !a.getStartTime().isAfter(to))
                .collect(Collectors.toList());

        // 2) Obtener todas las mediciones calculadas activas
        List<CalculatedMeasurement> calcList = calculatedMeasurementRepository.findByActiveTrueAndTenant(tenant);

        List<PatientHistoricalDTO> history = new ArrayList<>();

        for (Appointment app : appointments) {
            // mediciones de la cita
            List<Measurement> measurements = measurementRepository.findByAppointmentIdAndTenant(app.getId(), tenant);

            PatientHistoricalDTO dto = new PatientHistoricalDTO();
            dto.setDate(app.getStartTime());

            // MEDICIONES REALES
            Map<String, PatientHistoricalDTO.MeasurementDTO> measurementMap = measurements.stream()
                    .collect(Collectors.toMap(
                            m -> m.getMeasurementType().getSymbol(),
                            m -> {
                                PatientHistoricalDTO.MeasurementDTO mm = new PatientHistoricalDTO.MeasurementDTO();
                                mm.setValue(m.getValue());
                                mm.setUnit(m.getUnit() != null ? m.getUnit().getSymbol() : null);
                                return mm;
                            }
                    ));
            dto.setMeasurements(measurementMap);

            // MEDICIONES CALCULADAS
            Map<String, PatientHistoricalDTO.MeasurementDTO> calculatedMap = new HashMap<>();
            for (CalculatedMeasurement c : calcList) {
                iCalculatedMeasurementService.evaluateFormulaWithValues(c.getFormula(),
                                measurementMap.entrySet().stream()
                                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getValue())))
                        .ifPresent(v -> {
                            PatientHistoricalDTO.MeasurementDTO cm = new PatientHistoricalDTO.MeasurementDTO();
                            cm.setValue(v);
                            cm.setUnit(c.getResultUnit().getSymbol());
                            calculatedMap.put(c.getSymbol(), cm);
                        });
            }
            dto.setCalculated(calculatedMap);

            history.add(dto);
        }

        // opcional: ordenar por fecha ascendente
        history.sort(Comparator.comparing(PatientHistoricalDTO::getDate));

        return history;
    }
*/

    @Override
    public PatientDashboardSnapshotDTO getSnapshot(String tenant, Long patientId, boolean latest) {
        PatientDashboardSnapshotDTO dto = new PatientDashboardSnapshotDTO();

        List<Appointment> appointments = appointmentRepository.findByPatientIdAndTenantOrderByStartTimeDesc(patientId, tenant);
        if (appointments.isEmpty()) return dto;

        Appointment reference = latest ? appointments.get(0) : (appointments.size() > 1 ? appointments.get(1) : null);
        if (reference == null) return dto;

        List<Measurement> batch = measurementRepository.findByAppointmentIdAndTenant(reference.getId(), tenant);
        if (batch == null || batch.isEmpty()) return dto;

        // reales
        for (Measurement m : batch) {
            String key = m.getMeasurementType().getSymbol();
            PatientDashboardSnapshotDTO.MeasurementDTO mm = new PatientDashboardSnapshotDTO.MeasurementDTO();
            mm.setName(m.getMeasurementType().getName());
            mm.setValue(m.getValue());
            mm.setUnit(m.getUnit() != null ? m.getUnit().getSymbol() : null);
            dto.getMeasurements().put(key, mm);
        }

        // calculadas
        List<CalculatedMeasurement> calcList = calcRepo.findByActiveTrueAndTenant(tenant);
        Map<String, Double> dataMap = batch.stream()
                .collect(Collectors.toMap(m -> m.getMeasurementType().getSymbol(), Measurement::getValue));
        for (CalculatedMeasurement c : calcList) {
            Optional<Double> out = formulaService.evaluateFormulaWithValues(c.getFormula(), dataMap);
            out.ifPresent(val -> {
                PatientDashboardSnapshotDTO.MeasurementDTO cm = new PatientDashboardSnapshotDTO.MeasurementDTO();
                cm.setName(c.getName());
                cm.setValue(val);
                cm.setUnit(c.getResultUnit() != null ? c.getResultUnit().getSymbol() : null);
                dto.getCalculated().put(c.getSymbol(), cm);
            });
        }

        return dto;
    }

    @Override
    public List<PatientHistoricalDTO> getHistorical(String tenant, Long patientId, LocalDateTime from, LocalDateTime to) {
        List<Appointment> appointments = appointmentRepository.findByPatientIdAndTenantAndStartTimeBetweenOrderByStartTimeAsc(patientId, tenant, from, to);

        List<PatientHistoricalDTO> result = new ArrayList<>();

        for (Appointment app : appointments) {
            PatientHistoricalDTO dto = new PatientHistoricalDTO();
            dto.setDate(app.getStartTime());

            List<Measurement> measurements = measurementRepository.findByAppointmentIdAndTenant(app.getId(), tenant);
            for (Measurement m : measurements) {
                PatientHistoricalDTO.MeasurementDTO mdto = new PatientHistoricalDTO.MeasurementDTO();
                mdto.setValue(m.getValue());
                mdto.setUnit(m.getUnit() != null ? m.getUnit().getSymbol() : "");
                dto.getMeasurements().put(m.getMeasurementType().getSymbol(), mdto);
            }

            List<CalculatedMeasurement> calcList = calcRepo.findByActiveTrueAndTenant(tenant);
            Map<String, Double> dataMap = measurements.stream()
                    .collect(Collectors.toMap(m -> m.getMeasurementType().getSymbol(), Measurement::getValue));

            for (CalculatedMeasurement c : calcList) {
                Optional<Double> val = formulaService.evaluateFormulaWithValues(c.getFormula(), dataMap);
                val.ifPresent(v -> {
                    PatientHistoricalDTO.MeasurementDTO cdto = new PatientHistoricalDTO.MeasurementDTO();
                    cdto.setValue(v);
                    cdto.setUnit(c.getResultUnit() != null ? c.getResultUnit().getSymbol() : "");
                    dto.getCalculated().put(c.getSymbol(), cdto);
                });
            }

            result.add(dto);
        }

        return result;
    }

    @Override
    public List<KPIDTO> getKPIs(String tenant, Long patientId, LocalDateTime from, LocalDateTime to) {
        // para KPIs normalmente queremos latest y previous; aquí recuperamos history y armamos prev compare
        List<PatientHistoricalDTO> hist = getHistorical(tenant, patientId, from, to);
        if (hist.isEmpty()) return Collections.emptyList();

        PatientHistoricalDTO latest = hist.get(hist.size() - 1); // último en ascend => latest
        PatientHistoricalDTO prev = hist.size() > 1 ? hist.get(hist.size() - 2) : null;

        // fusionar keys de measured + calculated
        Set<String> keys = new LinkedHashSet<>();
        if (latest.getMeasurements() != null) keys.addAll(latest.getMeasurements().keySet());
        if (latest.getCalculated() != null) keys.addAll(latest.getCalculated().keySet());

        List<KPIDTO> kpis = new ArrayList<>();
        for (String key : keys) {
            KPIDTO k = new KPIDTO();
            k.setSymbol(key);
            // name: preferimos name de calculated or measured type; fallback key
            String name = null;
            if (latest.getMeasurements() != null && latest.getMeasurements().containsKey(key)) {
                name = getNameFromMeasurement(latest.getMeasurements().get(key), key);
                k.setValue(latest.getMeasurements().get(key).getValue());
                k.setUnit(latest.getMeasurements().get(key).getUnit());
            } else if (latest.getCalculated() != null && latest.getCalculated().containsKey(key)) {
                name = latest.getCalculated().get(key).getUnit() != null ? key : key;
                k.setValue(latest.getCalculated().get(key).getValue());
                k.setUnit(latest.getCalculated().get(key).getUnit());
            }
            k.setName(name != null ? name : key);

            if (prev != null) {
                Double prevValue = null;
                if (prev.getMeasurements() != null && prev.getMeasurements().containsKey(key))
                    prevValue = prev.getMeasurements().get(key).getValue();
                if (prev.getCalculated() != null && prev.getCalculated().containsKey(key) && prevValue == null)
                    prevValue = prev.getCalculated().get(key).getValue();
                k.setPrevValue(prevValue);
            }
            kpis.add(k);
        }
        return kpis;
    }

    private String getNameFromMeasurement(PatientHistoricalDTO.MeasurementDTO m, String fallback) {
        // Hay lugar para buscar el name real; en este DTO no tenemos el nombre del tipo,
        // pero el frontend puede mapear symbol->displayName. Aquí devolvemos fallback.
        return fallback;
    }

    @Override
    public List<EvolutionPointDTO> getEvolution(String tenant, Long patientId, LocalDateTime from, LocalDateTime to, List<String> metrics) {
        List<PatientHistoricalDTO> history = getHistorical(tenant, patientId, from, to);
        if (history.isEmpty()) return Collections.emptyList();

        List<EvolutionPointDTO> series = new ArrayList<>();
        for (PatientHistoricalDTO p : history) {
            for (String metric : metrics) {
                EvolutionPointDTO pt = new EvolutionPointDTO();
                pt.setDate(p.getDate());
                pt.setMetric(metric);
                PatientHistoricalDTO.MeasurementDTO m = (p.getMeasurements() != null) ? p.getMeasurements().get(metric) : null;
                if (m == null) {
                    // try calculated
                    PatientHistoricalDTO.MeasurementDTO c = (p.getCalculated() != null) ? p.getCalculated().get(metric) : null;
                    if (c != null) {
                        pt.setValue(c.getValue());
                        pt.setUnit(c.getUnit());
                    } else {
                        pt.setValue(null);
                    }
                } else {
                    pt.setValue(m.getValue());
                    pt.setUnit(m.getUnit());
                }
                series.add(pt);
            }
        }
        // optionally sort by date asc already done in getHistorical
        return series;
    }

    @Override
    public List<ComparisonResultDTO> comparePeriods(String tenant, Long patientId, LocalDateTime fromA, LocalDateTime toA, LocalDateTime fromB, LocalDateTime toB) {
        // obtener historiales
        List<PatientHistoricalDTO> a = getHistorical(tenant, patientId, fromA, toA);
        List<PatientHistoricalDTO> b = getHistorical(tenant, patientId, fromB, toB);

        // collect all keys
        Set<String> keys = new LinkedHashSet<>();
        a.forEach(d -> {
            if (d.getMeasurements() != null) keys.addAll(d.getMeasurements().keySet());
            if (d.getCalculated() != null) keys.addAll(d.getCalculated().keySet());
        });
        b.forEach(d -> {
            if (d.getMeasurements() != null) keys.addAll(d.getMeasurements().keySet());
            if (d.getCalculated() != null) keys.addAll(d.getCalculated().keySet());
        });

        List<ComparisonResultDTO> result = new ArrayList<>();
        for (String key : keys) {
            ComparisonResultDTO r = new ComparisonResultDTO();
            r.setSymbol(key);
            r.setName(key);

            // average for A
            Double avgA = averageForKey(a, key);
            Double avgB = averageForKey(b, key);
            r.setPeriodA_avg(avgA);
            r.setPeriodB_avg(avgB);
            if (avgA != null && avgB != null) r.setDelta(avgB - avgA);
            r.setUnit(determineUnitFromSeries(a, b, key));
            result.add(r);
        }
        return result;
    }

    private Double averageForKey(List<PatientHistoricalDTO> list, String key) {
        if (list == null || list.isEmpty()) return null;
        double s = 0;
        int c = 0;
        for (PatientHistoricalDTO p : list) {
            PatientHistoricalDTO.MeasurementDTO m = (p.getMeasurements() != null) ? p.getMeasurements().get(key) : null;
            if (m == null && p.getCalculated() != null) m = p.getCalculated().get(key);
            if (m != null && m.getValue() != null) {
                s += m.getValue();
                c++;
            }
        }
        return c == 0 ? null : s / c;
    }

    private String determineUnitFromSeries(List<PatientHistoricalDTO> a, List<PatientHistoricalDTO> b, String key) {
        if (a != null) {
            for (PatientHistoricalDTO p : a) {
                PatientHistoricalDTO.MeasurementDTO m = (p.getMeasurements() != null) ? p.getMeasurements().get(key) : null;
                if (m != null && m.getUnit() != null) return m.getUnit();
                PatientHistoricalDTO.MeasurementDTO c = (p.getCalculated() != null) ? p.getCalculated().get(key) : null;
                if (c != null && c.getUnit() != null) return c.getUnit();
            }
        }
        if (b != null) {
            for (PatientHistoricalDTO p : b) {
                PatientHistoricalDTO.MeasurementDTO m = (p.getMeasurements() != null) ? p.getMeasurements().get(key) : null;
                if (m != null && m.getUnit() != null) return m.getUnit();
                PatientHistoricalDTO.MeasurementDTO c = (p.getCalculated() != null) ? p.getCalculated().get(key) : null;
                if (c != null && c.getUnit() != null) return c.getUnit();
            }
        }
        return null;
    }

    @Override
    public Map<String, String> getAvailableMetrics(String tenant) {
        if (tenant == null || tenant.isEmpty()) {
            throw new IllegalArgumentException("Tenant is required");
        }

        List<MeasurementType> list = measurementTypeRepository.findByTenantAndActiveTrue(tenant);

        // Mantener orden predecible -> uso LinkedHashMap
        Map<String, String> map = new LinkedHashMap<>();
        for (MeasurementType mt : list) {
            String symbol = mt.getSymbol();
            String display = null;
            // preferir displayName si está seteado, sino construir con unidad si es posible
            if (mt.getDisplayName() != null && !mt.getDisplayName().trim().isEmpty()) {
                display = mt.getDisplayName();
            } else {
                if (mt.getDefaultUnit() != null && mt.getDefaultUnit().getSymbol() != null) {
                    display = mt.getName() + " (" + mt.getDefaultUnit().getSymbol() + ")";
                } else {
                    display = mt.getName();
                }
            }
            map.put(symbol, display);
        }
        return map;
    }

}
