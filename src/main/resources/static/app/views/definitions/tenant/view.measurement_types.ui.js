function MeasurementTypeUI() {
    return ViewConsultaBaseUI({
        formConsult: "formConsultMeasurementType",
        title: "Tipos de Medida",
        gridConsult: "gridConsultMeasurementType",
        gridPager: "gridPagerMeasurementType",
        formDetail: "formDetailMeasurementType",
        hiddenDelete: true,
        btnSave: "btnSaveMeasurementType",
        btnExcel:"btnExcelMeasurementType",
        btnNew:"btnNewMeasurementType",
        columns: [
            { id: "id", header: "ID", width: 40, hidden: true },
            { id: "symbol", header: "Símbolo", width: 200  },
            { id: "name", header: "Nombre", fillspace: true  },
            { id: "defaultUnitId", hidden: true },
            { id: "defaultUnitSymbol", hidden: true },
            { id: "defaultUnitName", hidden: true },
            { id: "defaultUnitSymbolName", header: "Unidad", width: 250  },
            {
                id: "active",
                header: "Activa",
                width: 60,
                adjust: "header",
                css: { "text-align": "center" },
                template: function (obj) {
                    return `<div style="display:flex; align-items:center; justify-content:center; height:100%;">
                                <span class="webix_icon ${obj.active ? "fa-check-square" : "fa-square"}"
                                      style="color:${obj.active ? "#1463fc" : "gray"}; font-size:16px;"></span>
                            </div>`;
                }
            }

        ],
        fields: [
            { view: "text", name: "id", label: "Id:", labelWidth: 100 },
            { view: "text", name: "symbol", label: "Símbolo:", labelWidth: 100, disabled: true},
            { view: "text", name: "name", label: "Nombre:", labelWidth: 100, invalidMessage: "Dede ingresar un Nombre", attributes: { maxlength: 100 } },
            {
                cols: [
                    {
                        view: "label",
                        label: "Unidad:",
                        width: 100
                    },
                    {
                        view: "text",
                        id: "defaultUnitId",
                        name: "defaultUnitId",
                        label: "",
                        labelWidth: 0,
                        gravity: 0.5,
                        disabled: true
                    },
                    {
                        view: "text",
                        id: "defaultUnitSymbol",
                        name: "defaultUnitSymbol",
                        invalidMessage: "Debe ingresar una Unidad",
                        attributes: { maxlength: 20 },
                        gravity: 1,
                    },
                    {
                        view: "icon",
                        icon: "fa-search",
                        tooltip: "Seleccionar Unidad",
                        width: 40,
                        align: "right",
                        id: "btnSelectUnit"
                    },
                    {
                        view: "label",
                        name: "defaultUnitName",
                        label: "",
                        css: { "font-weight": "bold", "margin-left": "8px" },
                        value: "",
                        gravity: 2,
                        height: 38
                    }
                ]
            },
            { view: "switch", name: "active", label: "Activa", labelWidth: 100 },
        ],
        rules:{
            "name": webix.rules.isNotEmpty,
            "defaultUnitSymbol": webix.rules.isNotEmpty
        }
    });
}
