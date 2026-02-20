class MeasurementType extends ViewConsultaBase {
    constructor() {
        super(
            { setValuesImpl: MeasurementType.getFunctionValuesImpl() },
            MeasurementType.getEndpoints()
        );
    }

    static getUIConfig() {
        return {
            formConsult: "formConsultMeasurementType",
            gridConsult: "gridConsultMeasurementType",
            gridPager: "gridPagerMeasurementType",
            formDetail: "formDetailMeasurementType",
            btnSave: "btnSaveMeasurementType",
            btnExcel: "btnExcelMeasurementType",
            btnNew: "btnNewMeasurementType"
        };
    }

    static register() {
        UtilesForm.registerInitializer(menu.K_MENU_MEASUREMENT_TYPES, (cellId) => {
            const instance = new MeasurementType();
            instance.init(MeasurementType.getUIConfig());
            instance.registerMultiviewReload(instance, cellId);

            instance.attachPopupSelector();
            instance.attachDefaultUnitBlur();

        });
    }

    static getFunctionValuesImpl() {
        return function(item) {
            this.formDetail.setValues({
                id: item.id,
                symbol: item.symbol,
                name: item.name,
                defaultUnitId: item.defaultUnitId,
                defaultUnitSymbol: item.defaultUnitSymbol,
                defaultUnitName: item.defaultUnitName,
                active: item.active,
            });
        };
    }

    static getEndpoints() {
        return {
            K_SERVICE_GET: services.K_SERVICE_MEASUREMENT_TYPE_GET,
            K_SERVICE_POST: services.K_SERVICE_MEASUREMENT_TYPE_POST,
            K_SERVICE_PUT: services.K_SERVICE_MEASUREMENT_TYPE_PUT,
            K_SERVICE_DELETE: services.K_SERVICE_MEASUREMENT_TYPE_DELETE
        };
    }

    attachPopupSelector() {
        if (!this.formDetail) return;
        $$("btnSelectUnit").attachEvent("onItemClick", () => this.openUnitSelector());
    }

    attachDefaultUnitBlur() {
        if (!this.formDetail) return;
        $$("defaultUnitSymbol").attachEvent("onBlur", () => this.defaultUnitSymbolBlur());
    }

    openUnitSelector() {
        const endpoint = services.K_SERVICE_UNIT_GET;

        const popup = new PopupSelect(endpoint, (selectedUnit) => {
            const form = $$("formDetailMeasurementType");
            form.setValues({ defaultUnitId: selectedUnit.id, defaultUnitSymbol: selectedUnit.symbol, defaultUnitName: selectedUnit.name }, true);
        }, {
            id: "popupSelectUnits",
            title: "Seleccionar Unidad",
            width: 700,
            columns: [
                { id: "id", header: "ID", width: 60, hidden: true },
                { id: "symbol", header: "Símbolo", width: 100 },
                { id: "name", header: "Nombre", fillspace: true },
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
            ]
        });

        popup.init();
    }

    async defaultUnitSymbolBlur() {
        const defaultUnitSymbol = this.formDetail.getValues().defaultUnitSymbol;
        if(defaultUnitSymbol !== ""){
            UtilesForm.showGlobalOverlay();
            try {
                await new Promise((resolve) => {
                    Utiles.secureRequest("GET", services.K_SERVICE_UNIT_GET + "/symbol/" + defaultUnitSymbol, "", (res) => {
                        if (res.status === 200 && res.data) {
                            this.formDetail.setValues({ defaultUnitId: res.data.id, defaultUnitSymbol: res.data.symbol, defaultUnitName: res.data.name }, true);
                        } else {
                            this.formDetail.setValues({ defaultUnitId: "", defaultUnitSymbol: "", defaultUnitName: "" }, true);
                            webix.alert({
                                title: "Error",
                                text: res.message || "",
                                type:"alert-error"
                            });
                        }
                        resolve();
                    });
                });
            } finally {
                UtilesForm.hideGlobalOverlay();
            }
        }else{
            this.formDetail.setValues({ defaultUnitId: "", defaultUnitSymbol: "", defaultUnitName: "" }, true);
        }
    }

    loadDataCallback(data) {
        const flattened = data.map(item => ({
            id: item.id,
            symbol: item.symbol,
            name: item.name,
            defaultUnitId: item.defaultUnit ? item.defaultUnit.id : null,
            defaultUnitSymbol: item.defaultUnit ? item.defaultUnit.symbol : "",
            defaultUnitName: item.defaultUnit ? item.defaultUnit.name : "",
            defaultUnitSymbolName: item.defaultUnit ? item.defaultUnit.symbol + " - " + item.defaultUnit.name : "",
            active: item.active
        }));

        this.gridConsult.clearAll();
        this.gridConsult.parse(flattened);
    }

    getValues() {
        const values = super.getValues();
        return {
            id: values.id,
            symbol: values.symbol,
            name: values.name,
            defaultUnit: values.defaultUnitId ? { id: values.defaultUnitId } : null,
            active: values.active
        };
    }

}


