class ListAppointments extends ListBase {
    constructor() {
        super(ListAppointments.getUIConfig(), ListAppointments.getEndpoints());
    }

    static getUIConfig() {
        return {
            gridList: "gridAppointments",
            formFilters: "formFilterAppointments",
            btnExcel: "btnExcelAppointments",
            btnNew: "btnNewAppointments",
            btnSearch: "btnSearchAppointments"
        };
    }

    static getEndpoints() {
        return {
            K_SERVICE_GET: services.K_SERVICE_APPOINTMENT_POST_LIST,
            K_SERVICE_PUT: services.K_SERVICE_APPOINTMENT_PUT,
            K_SERVICE_DELETE: services.K_SERVICE_APPOINTMENT_DELETE
        };
    }

    static register() {
        UtilesForm.registerInitializer(menu.K_MENU_APPOINTMENTS, (cellId) => {
            const instance = new ListAppointments();
            instance.init(ListAppointments.getUIConfig());
            instance.registerMultiviewReload(instance, cellId);
            instance.selectPatient();
            instance.onBlurPatient();
        });
    }

    registerMultiviewReload(instance, cellId) {
        const content = $$(menu.K_ELEMENTO_CONTENT);
        if (content) {
            content.attachEvent("onViewChange", (prev, next) => {
                if (next === cellId) instance.loadData();
            });
        }
    }

    selectPatient() {
        $$("btnSelectPatient").attachEvent("onItemClick", () => this.openPatientSelector());
    }

     onBlurPatient() {
         $$("patientId").attachEvent("onBlur", () => this.patientIdBlur());
     }

    async patientIdBlur() {
        const patientId = this.formFilters.getValues().patientId;
        if(Number(patientId) > 0){
            UtilesForm.showGlobalOverlay();
            try {
                await new Promise((resolve) => {
                    Utiles.secureRequest("GET", services.K_SERVICE_PATIENT_GET + "/" + Number(patientId), "", (res) => {
                        if (res.status === 200 && res.data) {
                            this.formFilters.setValues({ patientName: res.data.firstName + " " + res.data.lastName }, true);
                        } else {
                            this.formFilters.setValues({ patientId: "", patientName: "" }, true);
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
        }else{
            this.formFilters.setValues({ patientId: "", patientName: "" }, true);
        }
    }

    openPatientSelector() {
        const popup = new PopupSelect(
            services.K_SERVICE_PATIENT_GET,
            (selected) => {
                this.formFilters.setValues({
                    patientId: selected.id,
                    patientName: selected.firstName + " " + selected.lastName
                }, true);
            },
            {
                id: "popupSelectPatients",
                title: "Seleccionar Paciente",
                width: 700,
                columns: [
                    { id: "id", header: "ID", width: 60 },
                    { id: "firstName", header: "Nombre", width: 150 },
                    { id: "lastName", header: "Apellido", fillspace: true },
                    { id: "email", header: "Email", width: 200 },
                    {
                        id: "active",
                        header: "Activo",
                        width: 80,
                        template: obj => `<span class='webix_icon ${obj.active ? "fa-check-square" : "fa-square"}'
                            style='color:${obj.active ? "#1463fc" : "gray"}; font-size:16px;'></span>`
                    }
                ]
            }
        );
        popup.init();
    }

    newItem() {
        const editor = new EditorAppointment({
            title: "Nuevo",
            onSaved: (saved) => {
                if (typeof this.loadData === "function") {
                    this.loadData();
                } else if (this.grid && this.grid.clearAll) {
                    this.grid.clearAll();
                    this.loadData && this.loadData();
                }
            }
        });
        this.openEditor(editor);
    }

    async editItem(item) {
        UtilesForm.showGlobalOverlay();
        try {
            await new Promise((resolve) => {
                Utiles.secureRequest("GET", services.K_SERVICE_APPOINTMENT_GET + "/" + Number(item.id), "", (res) => {
                    if (res.status === 200 && res.data) {
                        const editor = new EditorAppointment({
                            title: "Modificar",
                            data: res.data,
                            onSaved: (saved) => {
                                if (typeof this.loadData === "function") {
                                    this.loadData();
                                }
                            }
                        });
                        this.openEditor(editor);
                    } else {
                        webix.alert({
                            title: "Error",
                            text: res.message,
                            type: "alert-error"
                        });
                    }
                    resolve();
                });
            });
        } finally {
            UtilesForm.hideGlobalOverlay();
        }
    }

    openEditor(editor) {
        editor.init(0 /* CITA */);
    }

    deleteItem(item) {
        webix.confirm({
            type:"confirm-warning",
            title: "Confirmar",
            ok: "Sí",
            cancel: "No",
            text: `¿Desea eliminar el registro ${item.id} del paciente ${item.patientName}?`
        }).then(() => {
            this.delete(item);
        });
    }

    loadDataCallback(data) {
        const mapped = data.map(d => ({
            id: d.id,
            date: d.startTime ? new Date(d.startTime) : null,
            time: d.startTime ? webix.Date.dateToStr("%H:%i")(new Date(d.startTime)) : "",
            patientId: d.patient ? d.patient.id: "",
            patientName: d.patient ? d.patient.firstName + " " + d.patient.lastName : "",
            status: d.status || "PENDING"
        }));

        this.grid.clearAll();
        this.grid.parse(mapped);
    }

    loadFilter() {
        const filters = this.formFilters ? this.formFilters.getValues() : {};
        if (filters.status === "ALL") {
            filters.status = "";
        }
        return filters;
    }

    setDefaults() {
        if (!this.formFilters) return;

        // from = hoy 00:00, to = hoy + 1 dia 00:00
        const now = new Date();
        const from = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 0, 0, 0, 0);
        const to = new Date(from.getTime() + 24 * 60 * 60 * 1000); // +1 día

        const fromStr = Utiles.formatForDatepicker ? Utiles.formatForDatepicker(from) : "";
        const toStr = Utiles.formatForDatepicker ? Utiles.formatForDatepicker(to) : "";

        const current = (this.formFilters.getValues && this.formFilters.getValues()) || {};
        const valuesToSet = Object.assign({}, current);

        valuesToSet.fromDate = from;
        valuesToSet.toDate = to;
        valuesToSet.status = "ALL";

        try {
            this.formFilters.setValues(valuesToSet, true);
        } catch (e) {
            webix.alert({
                title: "Error",
                text: "No se pudo setear valores por defecto: " + e,
                type:"alert-error"
            });
        }
    }

}

// Exponer globalmente
window.ListAppointments = ListAppointments;
