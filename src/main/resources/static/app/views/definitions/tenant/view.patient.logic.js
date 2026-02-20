class Patients extends ViewConsultaBase {
    constructor() {
        super(
            { setValuesImpl: Patients.getFunctionValuesImpl() },
            Patients.getEndpoints()
        );
    }

    static getUIConfig() {
        return {
            formConsult: "formConsultPatient",
            gridConsult: "gridConsultPatient",
            gridPager: "gridPagerPatient",
            formDetail: "formDetailPatient",
            btnSave: "btnSavePatient",
            btnExcel: "btnExcelPatient",
            btnNew: "btnNewPatient"
        };
    }

    static register() {
        UtilesForm.registerInitializer(menu.K_MENU_PATIENTS, (cellId) => {
            const instance = new Patients();
            instance.init(Patients.getUIConfig());
            instance.registerMultiviewReload(instance, cellId);
            instance.attachBirthDateOnChange();

            Patients.initContextMenu();
        });
    }

    attachBirthDateOnChange() {
        if (!this.formDetail) return;
        const birthDateField = $$("birthDate");
        birthDateField.attachEvent("onBlur", () => {
            const value = birthDateField.getValue();
            this.loadAge(value);
        });
    }

    loadAge(value) {
        if (!value) return;
        if (this.formDetail && this.formDetail.elements.age) {
            const age = Utiles.calculateAge(value);
            if(age > 0){
                this.formDetail.elements.age.setValue("Edad: " + age + " años");
            }else{
                this.formDetail.elements.age.setValue("");
            }
        }
    }

    static getFunctionValuesImpl() {
        return function(item) {
            this.formDetail.setValues({
                id: item.id,
                firstName: item.firstName,
                lastName: item.lastName,
                phone: item.phone,
                email: item.email,
                birthDate: item.birthDate,
                gender: item.gender,
                notes: item.notes,
                active: item.active
            });
            this.loadAge(item.birthDate);
        };
    }

    static getEndpoints() {
        return {
            K_SERVICE_GET: services.K_SERVICE_PATIENT_GET,
            K_SERVICE_POST: services.K_SERVICE_PATIENT_POST,
            K_SERVICE_PUT: services.K_SERVICE_PATIENT_PUT,
            K_SERVICE_DELETE: services.K_SERVICE_PATIENT_DELETE
        };
    }

    getValues() {
        const values = super.getValues();
        values.birthDate = Utiles.formatDateForServer(values.birthDate);
        return values;
    }

    static initContextMenu() {
        // Evitar crear el menú si ya existe
        if ($$("patientContextMenu")) return;

        // 1) creo menu contextual ligado a la grilla
        webix.ui({
            view: "contextmenu",
            id: "patientContextMenu",
            data: [
                { id: "viewDashboard", value: "Evolución del Paciente" }
            ],
            master: $$("gridConsultPatient")
        }).attachEvent("onMenuItemClick", function (id) {
            const grid = $$("gridConsultPatient");
            const selected = grid.getSelectedId();
            if (!selected) return;
            const item = grid.getItem(selected);
            if (id === "viewDashboard") {
                Patients.openDashboard(item.id, (item.firstName || "") + " " + (item.lastName || ""), Utiles.calculateAge(item.birthDate) || "");
            }
        });

        // 2) seleccionar fila al hacer clic derecho (onBeforeContextMenu)
        const grid = $$("gridConsultPatient");
        if (grid) {
            grid.attachEvent("onBeforeContextMenu", function (id) {
                // Como el id puede ser un objeto si se pasa evento; convierto a id real si es necesario
                try {
                    this.select(id);
                } catch (e) {
                    // selecciona el primer registro si no tengo ningúno seleccionado
                    const sel = this.getSelectedId();
                    if (!sel) {
                        const first = this.getFirstId();
                        if (first) this.select(first);
                    }
                }
                return true;
            });
        }
    }

    static openDashboard(patientId, patientName, patientAge) {
        DashboardEvolutionUI.open(patientId, patientName, patientAge);
    }
}
