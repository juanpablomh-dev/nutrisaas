class ListBase {
    constructor(config, endpoints) {
        this.config = config;
        this.endpoints = endpoints || {};
        this.grid = null;
        this.formFilters = null;
        this.btnExcel = null;
        this.btnNew = null;
        this.btnSearch = null;
    }

    init(uiConfig) {
        this.grid = $$(uiConfig.gridList);
        this.formFilters = $$(uiConfig.formFilters);
        this.btnExcel = $$(uiConfig.btnExcel);
        this.btnNew = $$(uiConfig.btnNew);
        this.btnSearch = $$(uiConfig.btnSearch);

        this.attachEvents();
        this.setDefaults();
        this.loadData();
    }

    attachEvents() {
        if (this.btnSearch) this.btnSearch.attachEvent("onItemClick", () => this.loadData());
        if (this.btnExcel) this.btnExcel.attachEvent("onItemClick", () => this.toExcel());
        if (this.btnNew) this.btnNew.attachEvent("onItemClick", () => this.newItem());

        if (this.grid) {

            // evento click (íconos de acción)
            this.grid.attachEvent("onItemClick", (id, e, trg) => {
                const clicked = e && e.target ? e.target : trg;
                if (clicked.closest && clicked.closest(".edit-icon")) {
                    this.editItem(this.grid.getItem(id));
                    return;
                }
                if (clicked.closest && clicked.closest(".delete-icon")) {
                    this.deleteItem(this.grid.getItem(id));
                    return;
                }
            });

           // evento doble clic sobre la fila → abrir editor
           this.grid.attachEvent("onItemDblClick", (id, e, trg) => {
               const item = this.grid.getItem(id);
               if (item) {
                   this.editItem(item);
               }
           });
        }
    }

    loadData() {
        if (!this.endpoints.K_SERVICE_GET) return;
        const filters = this.loadFilter();
        const method = this.formFilters ? "POST" : "GET";

        Utiles.secureRequest(method, this.endpoints.K_SERVICE_GET, filters, (data) => {
            this.loadDataCallback(data.data || []);
        });
    }

    loadFilter() {
        return this.formFilters ? this.formFilters.getValues() : {};
    }

    loadDataCallback(data) {
        if (!this.grid) return;
        this.grid.clearAll();
        this.grid.parse(data);
    }

    toExcel() {
        if (this.grid) webix.toExcel(this.grid);
    }

    newItem() {
        webix.message("Acción 'Nuevo' no implementada en la clase hija");
    }

    editItem(item) {
        webix.message("Acción 'Editar' no implementada en la clase hija");
    }

    deleteItem(item) {
        webix.message("Acción 'Editar' no implementada en la clase hija");
    }

    async delete(item) {
        UtilesForm.showGlobalOverlay();
        try {
            await new Promise((resolve) => {
                Utiles.secureRequest("DELETE", this.endpoints.K_SERVICE_DELETE + "/" + item.id, item, (res) => {
                    if (res.status === 200) {
                        this.grid.remove(item.id);
                        webix.alert("Cita/Turno eliminado");
                    } else {
                        webix.alert({
                            title: "Error",
                            text: res.message,
                            type:"alert-error"
                        });
                    }
                    resolve();
                });
            });
        } finally {
            UtilesForm.hideGlobalOverlay();
        }
    }

    setDefaults() {

    }


}


// Exponer globalmente
window.ListBase = ListBase;
