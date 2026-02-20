class EditorMeasurement {
    constructor(config = {}) {
        this.config = config;
        this.windowId = config.id || "windowEditorMeasurement";
        this.formId = config.formId || "formEditorMeasurement";
        this.data = config.data || null;
        this.callback = config.callback || null;
    }

    init() {
        webix.ui(EditorMeasurementUI(this.config)).show();
        this.form = $$(this.formId);
        this.btnSave = $$("btnSaveMeasurement");

        this.attachEvents();
        this.loadData(this.data);
    }
    attachEvents() {
        this.btnSave.attachEvent("onItemClick", () => this.save());

        const combo = $$("measurementTypeId");
        combo.attachEvent("onChange", (newVal) => {
            const type = this.config.measurementTypes?.find(t => t.id == newVal);
            if (type) {
                $$("measurementTypeName").setValue(type.name);
                $$("measurementUnitSymbol").setValue(type.defaultUnitSymbolName || "");
            }
        });
    }
    loadData(data) {
        if (!data) return;
        this.form.setValues(data);
        $$("measurementTypeId").disable();
    }
    save() {
        if (!this.form.validate()) {
            webix.alert({
                title: "Error",
                text: "Complete los campos obligatorios!",
                type:"alert-error"
            });
            return;
        }

        const values = this.form.getValues();
        const type = this.config.measurementTypes?.find(t => t.id == values.typeId);

        if (type) {
            values.unitId = type.defaultUnit ? type.defaultUnit.id : "";
            values.unitSymbol = type.defaultUnit ? type.defaultUnit.symbol : "";
        }else{
            webix.alert({
                title: "Error",
                text: "Error al obtener la unidad. El tipo de medida " + values.typeId + " no es válido o no tiene una unidad válida.",
                type:"alert-error"
            });
        }

        if (this.callback) {
            this.callback(values);
        }

        $$(this.windowId).close();
    }
}
