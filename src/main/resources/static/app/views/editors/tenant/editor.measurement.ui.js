function EditorMeasurementUI(config) {
    const isNew = !config.data;

    return {
        view: "window",
        id: config.id || "windowEditorMeasurement",
        position: "center",
        modal: true,
        width: 400,
        height: 300,
        head: {
            view: "toolbar",
            css: "my_toolbar",
            cols: [
                {
                    view: "label",
                    label: isNew ? "Agregar medición" : "Editar medición",
                    align: "left",
                    css: { "font-weight": "bold" }
                },
                {},
                {
                    view: "icon",
                    icon: "fa-close",
                    tooltip: "Cerrar",
                    click: function () {
                        this.getTopParentView().close();
                    }
                }
            ]
        },
        body: {
            rows: [
                {
                    view: "form",
                    id: config.formId || "formEditorMeasurement",
                    scroll: false,
                    padding: 15,
                    elementsConfig: { labelWidth: 80 },
                    elements: [
                        {
                            view: "combo",
                            id: "measurementTypeId",
                            name: "typeId",
                            label: "Tipo:",
                            required: true,
                            suggest: {
                                body: {
                                    template: "#displayName#",
                                    data: config.measurementTypes || []
                                }
                            }
                        },
                        {
                            view: "text",
                            name: "typeName",
                            id: "measurementTypeName",
                            hidden: true
                        },
                        {
                            view: "text",
                            name: "unitSymbol",
                            id: "measurementUnitSymbol",
                            hidden: true
                        },
                        {
                          view: "text",
                          name: "value",
                          label: "Valor:",
                          format:"1.111,00"
                        }
                    ]
                },
                {
                    view: "toolbar",
                    padding: { top: 10, right: 15, bottom: 10, left: 15 },
                    css: "my_toolbar",
                    cols: [
                        {},
                        {
                            view: "button",
                            id: "btnSaveMeasurement",
                            value: "Guardar",
                            css: "webix_primary",
                            width: 120
                        },
                        {
                            view: "button",
                            value: "Cancelar",
                            width: 100,
                            click: function () {
                                this.getTopParentView().close();
                            }
                        }
                    ]
                }
            ]
        }
    };
}
