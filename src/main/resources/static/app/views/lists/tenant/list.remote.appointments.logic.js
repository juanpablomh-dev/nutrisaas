class ListRemoteMeasurements extends ListBase {
    constructor() {
        super(ListRemoteMeasurements.getUIConfig(), ListRemoteMeasurements.getEndpoints());
    }

    static getUIConfig() {
        return {
            gridList: "gridAppointmentsRemote",
            formFilters: "formFilterAppointmentsRemote",
            btnExcel: "btnExcelAppointmentsRemote",
            btnNew: "btnNewAppointmentsRemote",
            btnSearch: "btnSearchAppointmentsRemote"
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
            const instance = new ListRemoteMeasurements();
            instance.init(ListRemoteMeasurements.getUIConfig());
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
        $$("btnSelectPatientRemote").attachEvent("onItemClick", () => this.openPatientSelector());
    }

     onBlurPatient() {
         $$("patientIdRemote").attachEvent("onBlur", () => this.patientIdBlur());
     }

    async patientIdBlur() {
        const patientId = this.formFilters.getValues().patientIdRemote;
        if(Number(patientId) > 0){
            UtilesForm.showGlobalOverlay();
            try {
                await new Promise((resolve) => {
                    Utiles.secureRequest("GET", services.K_SERVICE_PATIENT_GET + "/" + Number(patientId), "", (res) => {
                        if (res.status === 200 && res.data) {
                            this.formFilters.setValues({ patientNameRemote: res.data.firstName + " " + res.data.lastName }, true);
                        } else {
                            this.formFilters.setValues({ patientIdRemote: "", patientNameRemote: "" }, true);
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
            this.formFilters.setValues({ patientIdRemote: "", patientNameRemote: "" }, true);
        }
    }

    openPatientSelector() {
        const popup = new PopupSelect(
            services.K_SERVICE_PATIENT_GET,
            (selected) => {
                this.formFilters.setValues({
                    patientIdRemote: selected.id,
                    patientNameRemote: selected.firstName + " " + selected.lastName
                }, true);
            },
            {
                id: "popupSelectPatientsRemote",
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
        editor.init(1 /* MEDICIONES REMOTAS */);
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

        const filtersAux = {
            fromDate:    filters.fromDateRemote || null,
            toDate:      filters.toDateRemote || null,
            patientId:   filters.patientIdRemote || null,
            patientName: filters.patientNameRemote || null,
            status:      filters.statusRemote || null
        };

        if (filtersAux.status === "ALL") {
            filtersAux.status = "";
        }

        return filtersAux;
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

        valuesToSet.fromDateRemote = from;
        valuesToSet.toDateRemote = to;
        valuesToSet.statusRemote = "ALL";

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
window.ListRemoteMeasurements = ListRemoteMeasurements;
