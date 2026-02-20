function UnitsUI() {
    return ViewConsultaBaseUI({
        formConsult: "formConsultUnit",
        title: "Unidades",
        gridConsult: "gridConsultUnit",
        gridPager: "gridPagerUnit",
        formDetail: "formDetailUnit",
        hiddenDelete: true,
        btnSave: "btnSaveUnit",
        btnExcel: "btnExcelUnit",
        btnNew: "btnNewUnit",
        columns: [
            { id: "id", header: "ID", width: 40, hidden: true },
            { id: "symbol", header: "Símbolo", width: 80 },
            { id: "name", header: "Nombre", fillspace: true },
            {
                id: "active",
                header: "Activa",
                width: 60,
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
            { view: "text", name: "symbol", label: "Símbolo:", labelWidth: 100, invalidMessage: "Debe ingresar un símbolo", disabled: true },
            { view: "text", name: "name", label: "Nombre:", labelWidth: 100, invalidMessage: "Debe ingresar un nombre", attributes: { maxlength: 100 } },
            { view: "textarea", name: "description", label: "Descripción:", labelWidth: 100, height: 150, invalidMessage: "Debe ingresar una descripción", attributes: { maxlength: 255 } },
            { view: "switch", name: "active", label: "Activa", labelWidth: 100 },
            {
                view: "fieldset",
                label: "Auditoría",
                body: {
                    rows: [
                        {
                            view: "datepicker",
                            name: "createdAt",
                            label: "Fecha Creación:",
                            labelWidth: 140,
                            inputWidth: 350,
                            timepicker: true,
                            stringResult: false,
                            format: "%d/%m/%Y %H:%i:%s",
                            disabled: true
                        },
                        {
                            view: "datepicker",
                            name: "updatedAt",
                            label: "Fecha Modificación:",
                            labelWidth: 140,
                            inputWidth: 350,
                            timepicker: true,
                            stringResult: false,
                            format: "%d/%m/%Y %H:%i:%s",
                            disabled: true
                        }
                    ]
                }
            }
        ],
        rules: {
            "symbol": webix.rules.isNotEmpty,
            "name": webix.rules.isNotEmpty,
            "description": webix.rules.isNotEmpty
        }
    });
}
