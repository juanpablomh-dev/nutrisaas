class EditorAppointment {
    constructor(config) {
        this.config = config || {};
        this.formId = this.config.formId || "formEditorAppointment_00010";
        this.windowId = this.config.id || "windowEditorAppointment_00010";
        this.data = config.data || null;
    }

    init(type) {
        if ($$(this.windowId)) $$(this.windowId).destructor();
        webix.ui(EditorAppointmentUI(this.config)).show();

        this.type = type || 0;
        this.form = $$(this.formId);
        this.gridMeasurements = $$("gridMeasurements_00010");
        this.btnSave = $$("btnSaveAppointment_00010");
        this.allMeasurementsTypes = [];

        this.attachEvents();
        this.setDefaults();
        this.loadMeasurementsTypes();
        this.loadData(this.data);
    }

    attachEvents() {
        this.btnSave.attachEvent("onItemClick", () => this.save());
        $$("btnSelectPatient_00010").attachEvent("onItemClick", () => this.openPatientSelector());
        $$("patientId_00010").attachEvent("onBlur", () => this.patientIdBlur());
        $$("btnAddMeasurement_00010").attachEvent("onItemClick", () => this.openMeasurementEditor());
        if (this.gridMeasurements) {
            this.gridMeasurements.attachEvent("onItemDblClick", (id) => {
                const item = this.gridMeasurements.getItem(id);
                if (item) {
                    const popup = new EditorMeasurement({
                        measurementTypes: this.allMeasurementsTypes,
                        data: item,
                        callback: (measurement) => {
                            const grid = this.gridMeasurements;
                            if (!grid) return;

                            if (measurement.id) {
                                const existing = grid.getItem(measurement.id);
                                if (existing) grid.updateItem(measurement.id, measurement);
                                else grid.add(measurement);
                            } else {
                                grid.add(measurement);
                            }
                        }
                    });

                    popup.init();
                }
            });
        }
    }

    setDefaults() {
        if (!this.form) return;
        const now = new Date();
        const current = (this.form.getValues && this.form.getValues()) || {};
        const valuesToSet = Object.assign({}, current);
        valuesToSet.startDateTime_00010 = now;

        try {
            this.form.setValues(valuesToSet, true);
        } catch (e) {
            webix.alert({
                title: "Error",
                text: "No se pudo cargar valores por defecto: " + e,
                type:"alert-error"
            });
        }


    }

    async loadMeasurementsTypes() {
        UtilesForm.showGlobalOverlay();
        try {
            this.allMeasurementsTypes = await new Promise((resolve) => {
                Utiles.secureRequest( "GET", services.K_SERVICE_MEASUREMENT_TYPE_GET, "", (res) => {
                        if (res.status === 200 && Array.isArray(res.data)) {
                            resolve(res.data);
                        } else {
                            webix.alert({
                                title: "Error",
                                text: "No se pudieron obtener los tipos de medición.",
                                type: "alert-error"
                            });
                            resolve([]);
                        }
                    }
                );
            });
            UtilesForm.hideGlobalOverlay();
        } catch (err) {
            this.allMeasurementsTypes = [];
            UtilesForm.hideGlobalOverlay();
            webix.alert({
                title: "Error",
                text: "Error al cargar los tipos de medición: " + err,
                type: "alert-error"
            });
        }
    }

    loadData(data) {
        if (this.data) {
            const formValues = {
                id: data.id,
                startDateTime_00010: data.startTime ? new Date(data.startTime) : new Date(),
                patientId_00010: data.patient ? data.patient.id : "",
                patientName_00010: data.patient ? data.patient.firstName + " " + data.patient.lastName : "",
                status_00010: data.status || "PENDING",
                notes_00010: data.notes || ""
            };
            this.form.setValues(formValues);

            if (typeof window.updateNotesCounter === "function") {
              window.updateNotesCounter(formValues.notes_00010, this.form);
            }

            if (data.measurements) {
                const mapped = data.measurements.map(m => ({
                    id: m.id,
                    typeId: m.measurementType?.id || null,
                    typeName: m.measurementType?.name || "",
                    value: m.value,
                    unitId: m.unit?.id || null,
                    unitSymbol: m.unit?.symbol || ""
                }));
                this.gridMeasurements.parse(mapped);
            } else {
                this.gridMeasurements.clearAll();
            }
        }
    }

    async patientIdBlur() {
        const patientId = this.form.getValues().patientId_00010;
        if (Number(patientId) > 0) {
            UtilesForm.showGlobalOverlay();
            try {
                await new Promise((resolve) => {
                    Utiles.secureRequest("GET", services.K_SERVICE_PATIENT_GET + "/" + Number(patientId), "", (res) => {
                        if (res.status === 200 && res.data) {
                            this.form.setValues({ patientName_00010: res.data.firstName + " - " + res.data.lastName }, true);
                        } else {
                            this.form.setValues({ patientId_00010: "", patientName_00010: "" }, true);
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
        } else {
            this.form.setValues({ patientId_00010: "", patientName_00010: "" }, true);
        }
    }

    openPatientSelector() {
        const popup = new PopupSelect(
            services.K_SERVICE_PATIENT_GET,
            (selected) => {
                this.form.setValues({
                    patientId_00010: selected.id,
                    patientName_00010: selected.firstName + " " + selected.lastName
                }, true);
            },
            {
                id: "popupSelectPatients_00010",
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

    async openMeasurementEditor() {
        try {
            let measurementTypes = [];
            try {
                const existingRows = (this.gridMeasurements && typeof this.gridMeasurements.serialize === "function")
                    ? this.gridMeasurements.serialize()
                    : [];
                const existingTypeIds = new Set(
                    existingRows
                        .map(r => r.typeId ?? (r.type && r.type.id) ?? null)
                        .filter(v => v !== null && v !== undefined && v !== "")
                        .map(v => Number(v))
                );

                measurementTypes = this.allMeasurementsTypes.filter(t => {
                    const backendTypeId = t.id ?? t.typeId ?? (t.type && t.type.id) ?? null;
                    if (backendTypeId === null || backendTypeId === undefined || backendTypeId === "") return true;
                    return !existingTypeIds.has(Number(backendTypeId));
                });


            } catch (e) {
                console.warn("Error filtrando allMeasurementsTypes:", e);
                measurementTypes = [];
            }

            if (!measurementTypes.length) return;

            const popup = new EditorMeasurement({
                measurementTypes,
                callback: (measurement) => {
                    const grid = this.gridMeasurements;
                    if (!grid) return;

                    if (measurement.id) {
                        const existing = grid.getItem(measurement.id);
                        if (existing) grid.updateItem(measurement.id, measurement);
                        else grid.add(measurement);
                    } else {
                        measurement.id = Utiles.generateUUID();
                        grid.add(measurement);
                    }
                }
            });

            popup.init();

        } catch (err) {
            webix.alert({
                title: "Error",
                text: "Error al cargar los tipos de medición: " + err,
                type: "alert-error"
            });
        }
    }

    save() {
        if (!this.form.validate()) {
            webix.alert({
                title: "Error",
                text: "Complete los campos obligatorios.",
                type:"alert-error"
            });
            return;
        }

        const values = this.form.getValues();
        const measures = this.gridMeasurements.serialize().map(m => ({
            id: m.id,
            measurementType: { id: m.typeId },
            value: parseFloat(m.value || 0),
            unit: m.unitId ? { id: m.unitId } : null
        }));

        const payload = {
            id: values.id || null,
            startTime: Utiles.fixDateTimeForBackend(values.startDateTime_00010),
            patient: { id: Number(values.patientId_00010) },
            status: values.status_00010,
            notes: values.notes_00010,
            type: this.type,
            measurements: measures
        };

        UtilesForm.showGlobalOverlay();

        const method = payload.id ? "PUT" : "POST";
        const endpoint = payload.id
            ? services.K_SERVICE_APPOINTMENT_PUT
            : services.K_SERVICE_APPOINTMENT_POST;

        Utiles.secureRequest(method, endpoint, payload, (res) => {
            UtilesForm.hideGlobalOverlay();
            if (res.status === 200 || res.status === 201) {
                webix.alert("Datos guardados correctamente.");

                // notificar al invocador (si pasó el callback)
                try {
                    if (this.config && typeof this.config.onSaved === "function") {
                        // paso el objeto devuelto por el backend (res.data)
                        this.config.onSaved(res.data);
                    }
                } catch (e) {
                    console.warn("onSaved callback falló:", e);
                }

                // cerrar ventana editor
                const win = $$(this.windowId);
                if (win) win.close();
            } else {
                webix.alert({
                    title: "Error",
                    text: "Error al guardar: " + res.message,
                    type:"alert-error"
                });
            }
        });
    }

    async delete(item) {
        if (!item) return;
        const confirmed = await new Promise((resolve) => {
            webix.confirm(
                            {
                                title: "Confirmar eliminación",
                                text: `¿Deseas eliminar el registro #${item.id}?`,
                                type:"confirm-warning",
                                ok: "Eliminar",
                                cancel: "Cancelar",
                                callback: function(result) { resolve(result); }
                            }
            );
        });
        if (!confirmed) return;

        webix.alert("Eliminando...");

        const headers = { "Content-Type": "application/json" };
        if (window.csrfToken) headers["X-CSRF-Token"] = window.csrfToken;

        const urlWithId = `${K_SERVICE_APPOINTMENT_DELETE.replace(/\/$/, "")}/${encodeURIComponent(item.id)}`;

        try {
            let response = await fetch(urlWithId, { method: "DELETE", headers });

            if (response.ok) {
                if (this.grid && typeof this.grid.remove === "function") {
                    this.gridMeasurements.remove(item.id);
                }
                webix.alert("Registro eliminado correctamente.");
                return;
            }

            if (response.status === 404 || response.status === 405 || response.status === 400) {
                const fallbackResp = await fetch(K_SERVICE_APPOINTMENT_DELETE, {
                    method: "POST",
                    headers,
                    body: JSON.stringify({ id: item.id })
                });

                if (fallbackResp.ok) {
                    if (this.grid && typeof this.grid.remove === "function") {
                        this.gridMeasurements.remove(item.id);
                    }
                    webix.alert("Registro eliminado correctamente.");
                    return;
                }
                const text = await fallbackResp.text().catch(()=>null);
                throw new Error(`Fallback failed: ${fallbackResp.status} ${text || ""}`);
            }

            const errText = await response.text().catch(()=>null);
            throw new Error(`Error en el servidor: ${response.status} ${errText || ""}`);
        } catch (err) {
            webix.alert({
                title: "Error",
                text: "No se pudo eliminar el registro. " + err,
                type:"alert-error"
            });
        }
    }
}
