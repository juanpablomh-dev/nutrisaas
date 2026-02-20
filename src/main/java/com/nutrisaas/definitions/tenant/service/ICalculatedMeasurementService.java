package com.nutrisaas.definitions.tenant.service;

import com.nutrisaas.definitions.tenant.model.CalculatedMeasurement;
import com.nutrisaas.definitions.tenant.model.Unit;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ICalculatedMeasurementService {

    /**
     * Evalúa la fórmula del CalculatedMeasurement identificado por {@code calcId}
     * para el paciente {@code patientId} y tenant {@code tenant}.
     *
     * @param calcId    id de la fórmula guardada
     * @param patientId id del paciente para el que se evaluará la fórmula
     * @param tenant    identificador del tenant
     * @return Optional con el resultado numérico si se pudo evaluar; Optional.empty() si faltan datos
     */
    Optional<Double> evaluateById(Long calcId, Long patientId, String tenant);

    /**
     * Evalúa una fórmula textual (no necesariamente persistida) usando MeasurementType.symbol
     * como variables. Busca las mediciones del paciente para cada variable.
     *
     * @param formula    expresión matemática (ej. "WEIGHT / (HEIGHT * HEIGHT)")
     * @param resultUnit unidad esperada del resultado (puede ser null si no aplica)
     * @param patientId  id del paciente
     * @param tenant     identificador del tenant
     * @return Optional con el resultado numérico si se pudo evaluar; Optional.empty() si faltan datos
     */
    Optional<Double> evaluateFormula(String formula, Unit resultUnit, Long patientId, String tenant);


    Optional<Double> evaluateFormulaWithValues(String formula, Map<String, Double> dataMap);

    /**
     * Guarda un nuevo CalculatedMeasurement en la base.
     *
     * @param cm entidad a guardar
     * @return la entidad persistida
     */
    CalculatedMeasurement save(CalculatedMeasurement cm);

    /**
     * Actualiza un CalculatedMeasurement existente.
     *
     * @param id      id de la entidad a actualizar
     * @param payload datos con los que actualizar
     * @return la entidad actualizada
     */
    CalculatedMeasurement update(Long id, CalculatedMeasurement payload);

    /**
     * Lista las fórmulas definidas para un tenant, ordenadas por nombre.
     *
     * @param tenant identificador del tenant
     * @return lista de CalculatedMeasurement
     */
    List<CalculatedMeasurement> listByTenant(String tenant);

    /**
     * Busca un CalculatedMeasurement por su id.
     *
     * @param id identificador
     * @return Optional con la entidad si existe
     */
    Optional<CalculatedMeasurement> findById(Long id);
}
