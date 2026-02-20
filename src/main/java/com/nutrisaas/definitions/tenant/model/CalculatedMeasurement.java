package com.nutrisaas.definitions.tenant.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "calculated_measurements")
@Data
public class CalculatedMeasurement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String symbol;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 500)
    private String description;

    // Ej: "WEIGHT / (HEIGHT * HEIGHT)"
    @Column(nullable = false, columnDefinition = "TEXT")
    private String formula;

    @ManyToOne(optional = false)
    @JoinColumn(name = "result_unit_id", nullable = false)
    private Unit resultUnit;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private String tenant;

    private String displayName;

    public String getDisplayName() {
        if (resultUnit != null && resultUnit.getSymbol() != null) {
            return name + " (" + resultUnit.getSymbol() + ")";
        }
        return name;
    }

    public void loadFromEntityToUpdate(CalculatedMeasurement src) {
        this.name = src.getName();
        this.description = src.getDescription();
        this.formula = src.getFormula();
        this.resultUnit = src.getResultUnit();
        this.active = src.getActive();
    }
}
