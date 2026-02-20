package com.nutrisaas.definitions.tenant.controller;

import com.nutrisaas.definitions.tenant.model.CalculatedMeasurement;
import com.nutrisaas.definitions.tenant.service.CalculatedMeasurementService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tenant/calculated")
@AllArgsConstructor
public class CalculatedMeasurementController {

    private final CalculatedMeasurementService calcService;


    // Listar por tenant
    @GetMapping
    public List<CalculatedMeasurement> list(@RequestParam String tenant) {
        return calcService.listByTenant(tenant);
    }

    // Obtener por id
    @GetMapping("/{id}")
    public ResponseEntity<CalculatedMeasurement> get(@PathVariable Long id) {
        return calcService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear
    @PostMapping
    public ResponseEntity<CalculatedMeasurement> create(@RequestBody CalculatedMeasurement payload) {
        CalculatedMeasurement saved = calcService.save(payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<CalculatedMeasurement> update(@PathVariable Long id, @RequestBody CalculatedMeasurement payload) {
        CalculatedMeasurement updated = calcService.update(id, payload);
        return ResponseEntity.ok(updated);
    }

    // Evaluar (por id de fórmula) para un paciente
    @GetMapping("/{id}/evaluate")
    public ResponseEntity<?> evaluate(@PathVariable Long id,
                                      @RequestParam Long patientId,
                                      @RequestParam String tenant) {
        Optional<Double> result = calcService.evaluateById(id, patientId, tenant);
        if (result.isPresent()) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("value", result.get());
            resp.put("calculatedMeasurementId", id);
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("error", "Missing data or unable to evaluate formula"));
        }
    }

    // Evaluar fórmula ad-hoc (sin crear en BD)
    @PostMapping("/evaluate")
    public ResponseEntity<?> evaluateFormula(@RequestBody Map<String, Object> body) {
        // body: { "formula":"WEIGHT/(HEIGHT*HEIGHT)", "patientId":123, "tenant":"t1" }
        String formula = (String) body.get("formula");
        Long patientId = ((Number) body.get("patientId")).longValue();
        String tenant = (String) body.get("tenant");
        Optional<Double> res = calcService.evaluateFormula(formula, null, patientId, tenant);
        return res.map(r -> ResponseEntity.ok(Map.of("value", r)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build());
    }
}
