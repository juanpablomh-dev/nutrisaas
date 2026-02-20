package com.nutrisaas.definitions.tenant.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "measurement_types")
@Data
public class MeasurementType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String symbol;

    @Column(nullable = false, unique = true)
    private String name; // Ej: "Altura", "Cintura", "Peso"

    @ManyToOne(optional = false)
    @JoinColumn(name = "default_unit_id", nullable = false)
    private Unit defaultUnit;

    @Column(nullable = false, updatable = false)
    private String tenant;

    @Column(nullable = false)
    private Boolean active = true;

    private String displayName; // Ej: "Weight (cm)", "Height (cm)", "BMI (%)"

    public void loadFromEntityToUpdate(MeasurementType measurementType) {
        this.name = measurementType.getName();
        this.defaultUnit = measurementType.getDefaultUnit();
        this.active = measurementType.getActive();
    }

    public String getDisplayName() {
        if (defaultUnit != null && defaultUnit.getSymbol() != null) {
            return name + " (" + defaultUnit.getSymbol() + ")";
        }
        return name;
    }

}
