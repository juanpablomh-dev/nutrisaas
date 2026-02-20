if (typeof ViewConsultaBase === "undefined") {
    class ViewConsultaBase {
        constructor(config, endpoints) {
            this.setValuesImpl = config.setValuesImpl || function(item) {
                this.formDetail.setValues(item);
            };

            if (endpoints) {
                this.K_SERVICE_GET = endpoints.K_SERVICE_GET;
                this.K_SERVICE_POST = endpoints.K_SERVICE_POST;
                this.K_SERVICE_PUT = endpoints.K_SERVICE_PUT;
                this.K_SERVICE_DELETE = endpoints.K_SERVICE_DELETE;
            }

            this.eventsAttached = false;
        }

        registerMultiviewReload(instance, cellId) {
            const content = $$(menu.K_ELEMENTO_CONTENT);
            content.attachEvent("onViewChange", (prevId, nextId) => {
                if (nextId === cellId) instance.loadData();
            });
        }

        init(uiConfig) {
            this.formConsult = $$(uiConfig.formConsult);
            this.gridConsult = $$(uiConfig.gridConsult);
            this.formDetail = $$(uiConfig.formDetail);
            this.btnSave = $$(uiConfig.btnSave);
            this.btnNew = $$(uiConfig.btnNew);
            this.btnExcel = $$(uiConfig.btnExcel);

            if (!this.formConsult) {
                console.error("UI components not found:", uiConfig);
                return;
            }

            if (!this.eventsAttached) {
                const colConfig = this.gridConsult.getColumnConfig("delete");
                if (colConfig && !colConfig.hidden) {
                    this.gridConsult.attachEvent("onItemClick", (id, e, trg) => this.deleteItem(id, e, trg));
                }

                this.gridConsult.attachEvent("onAfterSelect", (id) => this.itemSelect(id));
                this.btnNew.attachEvent("onItemClick", () => this.onNew());
                this.btnSave.attachEvent("onItemClick", () => this.save());
                this.btnExcel.attachEvent("onItemClick", () => this.toExcel());

                this.eventsAttached = true;
            }

            if (this.formDetail.elements.id) {
                this.formDetail.elements.id.hide();
            }
        }

        // --- ACCIONES ----------------------------------

        deleteItem(id, e, trg) {
            if (trg.classList.contains("fa-trash")) {
                const rowId = id.row;
                webix.confirm({
                    title: "Confirmar",
                    ok: "Sí",
                    cancel: "No",
                    text: "¿Desea eliminar este registro?",
                    type:"confirm-warning"
                }).then(() => {
                    const formDetail = this.formDetail.getValues().id;
                    const rowIdStr = typeof formDetail === "string" ? String(rowId) : rowId;
                    if (formDetail === rowIdStr) this.formDetail.clear();
                    this.gridConsult.remove(rowId);
                });
            }
        }

        itemSelect(id) {
            const item = this.gridConsult.getItem(id);
            this.changeValueButtonSave(true);
            this.formDetail.clearValidation();
            this.setValues(item);
            this.highlightForm();
        }

        onNew() {
            this.formDetail.clearValidation();
            this.formDetail.clear();
            this.formDetail.setValues({ id: null });
            this.formDetail.config.isNew = true;
            this.changeValueButtonSave(false);
            this.resetFormHighlight();
        }

        async save() {
            if (!this.formDetail.validate()) {
                webix.alert({
                    title: "Error",
                    text: "Complete los campos obligatorios!",
                    type:"alert-error"
                });
                return;
            }

            this.btnSave.disable();
            UtilesForm.showGlobalOverlay();

            try {
                const values = this.getValues();

                if (Number(values.id) > 0) {
                    await this.update(values);
                } else {
                    await this.create(values);
                }
            } finally {
                this.btnSave.enable();
                UtilesForm.hideGlobalOverlay();
            }
        }

        toExcel() {
            webix.toExcel(this.gridConsult);
        }
        // -------------------------------------------------

        loadData() {
            Utiles.secureRequest("GET", this.K_SERVICE_GET, "", (data) => {
                this.loadDataCallback(data.data);
            });
        }

        loadDataCallback(data) {
            this.gridConsult.clearAll();
            this.gridConsult.parse(data);
        }

        clearForm() {
            this.formDetail.clear();
            this.formDetail.setValues({ id: null });
        }

        setValues(item) {
            this.setValuesImpl.call(this, item);
        }

        getValues() {
            return this.formDetail.getValues();
        }

        create(values) {
            return new Promise((resolve) => {
                Utiles.secureRequest("POST", this.K_SERVICE_POST, values, (res) => {
                    if ((res.status === 200 || res.status === 201) && res.data) {
                        this.loadData();
                        this.clearForm();
                        this.changeValueButtonSave(false);
                        this.formDetail.clearValidation();
                        webix.alert("Registro creado correctamente");
                    } else {
                        webix.alert({
                            title: "Error",
                            text: res.message || "",
                            type:"alert-error"
                        });
                    }
                    resolve();
                });
            });
        }

        update(values) {
            return new Promise((resolve) => {
                Utiles.secureRequest("PUT", this.K_SERVICE_PUT + "/" + values.id, values, (res) => {
                    if (res.status === 200 && res.data) {
                        const index = this.gridConsult.getIndexById(res.data.id);
                        if (index !== -1) {
                            this.gridConsult.updateItem(res.data.id, res.data);
                            this.gridConsult.select(res.data.id);
                        }
                        this.clearForm();
                        this.changeValueButtonSave(false);
                        this.formDetail.clearValidation();
                        webix.alert("Registro actualizado correctamente");
                    } else {
                        webix.alert({
                            title: "Error",
                            text: res.message || "",
                            type:"alert-error"
                        });
                    }
                    resolve();
                });
            });
        }

        highlightForm() {
            this.formDetail.$view.classList.add("form-highlight");
        }

        resetFormHighlight() {
            this.formDetail.$view.classList.remove("form-highlight");
        }

        changeValueButtonSave(update) {
            this.btnSave.setValue(update ? "Modificar" : "Agregar");
        }
    }

    // Hago esto para que no de error cuando varias views crean la clase base
    window.ViewConsultaBase = ViewConsultaBase;
}