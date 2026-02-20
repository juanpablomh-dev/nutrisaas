package com.nutrisaas.definitions.tenant.service;

import com.nutrisaas.definitions.tenant.model.CalculatedMeasurement;
import com.nutrisaas.definitions.tenant.model.Measurement;
import com.nutrisaas.definitions.tenant.model.Unit;
import com.nutrisaas.definitions.tenant.repository.CalculatedMeasurementRepository;
import com.nutrisaas.definitions.tenant.repository.MeasurementRepository;
import com.nutrisaas.definitions.tenant.repository.UnitRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class CalculatedMeasurementService implements ICalculatedMeasurementService {

    private final CalculatedMeasurementRepository calcRepo;
    private final MeasurementRepository measurementRepository;
    private final UnitRepository unitRepository;

    /**
     * Evalúa la fórmula de un calculatedMeasurement para un paciente y tenant.
     * Retorna Optional.empty() si faltan datos necesarios (o lanza excepción si preferís).
     */

    public Optional<Double> evaluateById(Long calcId, Long patientId, String tenant) {
        CalculatedMeasurement cm = calcRepo.findById(calcId)
                .orElseThrow(() -> new NoSuchElementException("CalculatedMeasurement not found: " + calcId));
        return evaluateFormula(cm.getFormula(), cm.getResultUnit(), patientId, tenant);
    }

    /**
     * Evalúa una fórmula textual dada, usando MeasurementType.symbol como variables.
     * - Busca la última medición del paciente para cada variable.
     * - Si existe conversionService, intentará convertir unidad de la medición al unit esperado por MeasurementType.defaultUnit (opcional).
     */
    public Optional<Double> evaluateFormula(String formula, Unit resultUnit, Long patientId, String tenant) {
        // 1) extraer variables (tokens). Asumimos variables con formato \b[A-Z0-9_]+\b
        Set<String> variables = extractVariables(formula);
        if (variables.isEmpty()) return Optional.empty();

        // 2) obtener valores para cada variable
        Map<String, Double> context = new HashMap<>();
        for (String var : variables) {
            // buscar la última measurement para patient y measurementType.symbol = var
            Optional<Measurement> optionalMeasurement = measurementRepository.findTopByPatientIdAndMeasurementType_SymbolAndTenantOrderByMeasuredAtDesc(patientId, var, tenant);
            if (optionalMeasurement.isEmpty()) {
                // dato faltante -> no se puede evaluar
                return Optional.empty();
            }
            Measurement m = optionalMeasurement.get();

            double value = m.getValue();
            // si tienes conversion logic y measurementType.defaultUnit y result / expected units, convertir aquí
            // ejemplo: si MeasurementType.defaultUnit != expectedUnit -> convert
            // Para simplicidad, asumimos que Measurement values ya están en unidades esperadas por las fórmulas
            // Si necesitas convertir:
            // if (conversionService != null) value = conversionService.convert(value, m.getUnit().getSymbol(), expectedSymbol).orElseThrow(...);

            context.put(var, value);
        }

        // 3) evaluar con exp4j
        try {
            net.objecthunter.exp4j.ExpressionBuilder builder = new net.objecthunter.exp4j.ExpressionBuilder(formula);
            // añade las variables
            builder = builder.variables(variables);
            net.objecthunter.exp4j.Expression expr = builder.build();

            for (Map.Entry<String, Double> e : context.entrySet()) {
                expr.setVariable(e.getKey(), e.getValue());
            }

            double result = expr.evaluate();
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                return Optional.empty();
            }

            return Optional.of(result);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error evaluating formula: " + ex.getMessage(), ex);
        }
    }

    public Optional<Double> evaluateFormulaWithValues(String formula, Map<String, Double> dataMap) {
        if (dataMap == null || dataMap.isEmpty()) return Optional.empty();

        // extraer variables de la fórmula
        Set<String> variables = extractVariables(formula);
        if (variables.isEmpty()) return Optional.empty();

        // verificar que todas las variables existan en dataMap
        for (String var : variables) {
            if (!dataMap.containsKey(var)) return Optional.empty();
        }

        try {
            net.objecthunter.exp4j.ExpressionBuilder builder = new net.objecthunter.exp4j.ExpressionBuilder(formula);
            builder = builder.variables(variables);
            net.objecthunter.exp4j.Expression expr = builder.build();

            for (String var : variables) {
                expr.setVariable(var, dataMap.get(var));
            }

            double result = expr.evaluate();
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                return Optional.empty();
            }

            return Optional.of(result);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error evaluating formula: " + ex.getMessage(), ex);
        }
    }

    private Set<String> extractVariables(String formula) {
        Set<String> vars = new HashSet<>();
        // regex: tokens en mayúsculas y guiones bajos y dígitos
        Pattern p = Pattern.compile("\\b([A-Z][A-Z0-9_]*)\\b");
        Matcher m = p.matcher(formula);
        while (m.find()) {
            vars.add(m.group(1));
        }
        return vars;
    }

    // Métodos CRUD sencillos
    public CalculatedMeasurement save(CalculatedMeasurement cm) {
        return calcRepo.save(cm);
    }

    public CalculatedMeasurement update(Long id, CalculatedMeasurement payload) {
        CalculatedMeasurement stored = calcRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Not found"));
        stored.loadFromEntityToUpdate(payload);
        stored.setFormula(payload.getFormula());
        stored.setResultUnit(payload.getResultUnit());
        stored.setDisplayName(payload.getDisplayName());
        return calcRepo.save(stored);
    }

    public List<CalculatedMeasurement> listByTenant(String tenant) {
        return calcRepo.findByTenantOrderByNameAsc(tenant);
    }

    public Optional<CalculatedMeasurement> findById(Long id) {
        return calcRepo.findById(id);
    }
}
