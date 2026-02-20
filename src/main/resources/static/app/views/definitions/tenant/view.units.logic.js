class Units extends ViewConsultaBase {
    constructor() {
        super(
            { setValuesImpl: Units.getFunctionValuesImpl() },
            Units.getEndpoints()
        );
    }

    static getUIConfig() {
        return {
            formConsult: "formConsultUnit",
            gridConsult: "gridConsultUnit",
            gridPager: "gridPagerUnit",
            formDetail: "formDetailUnit",
            btnSave: "btnSaveUnit",
            btnExcel: "btnExcelUnit",
            btnNew: "btnNewUnit"
        };
    }

    static register() {
        UtilesForm.registerInitializer(menu.K_MENU_UNITS, (cellId) => {
            const instance = new Units();
            instance.init(Units.getUIConfig());
            instance.registerMultiviewReload(instance, cellId);
        });
    }

    static getFunctionValuesImpl() {
        return function(item) {
            this.formDetail.setValues({
                id: item.id,
                name: item.name,
                symbol: item.symbol,
                description: item.description,
                active: item.active,
                createdAt: item.createdAt ? new Date(item.createdAt) : null,
                updatedAt: item.updatedAt ? new Date(item.updatedAt) : null
            });
        };
    }

    static getEndpoints() {
        return {
            K_SERVICE_GET: services.K_SERVICE_UNIT_GET,
            K_SERVICE_POST: services.K_SERVICE_UNIT_POST,
            K_SERVICE_PUT: services.K_SERVICE_UNIT_PUT,
            K_SERVICE_DELETE: services.K_SERVICE_UNIT_DELETE
        };
    }
}
