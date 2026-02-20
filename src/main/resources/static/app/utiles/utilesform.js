
function UtilesForm() {}


UtilesForm.setupSidebarNavigation = function(sidebar, content) {
    sidebar.attachEvent(evento.K_EVENTO_AFTER_SELECT, function(id) {
        const initializer = UtilesForm.initializers[id];
        UtilesForm.setupView(sidebar, content, id, initializer);
    });
};

UtilesForm.initializers = {};

UtilesForm.registerInitializer = function(viewId, initializer) {
    UtilesForm.initializers[viewId] = initializer;
};

UtilesForm.loadScriptsView = async function(id) {
    switch(id) {
        case menu.K_MENU_DASHBOARD:
            await  UtilesForm.loadScripts([
                "/app/views/core/tenant/view.dashboard.ui.js",
                "/app/views/core/tenant/view.dashboard.logic.js"
            ]);
            break;
        case menu.K_MENU_UNITS:
            await  UtilesForm.loadScripts([
                "/app/views/definitions/base/view.consulta.base.ui.js",
                "/app/views/definitions/base/view.consulta.base.logic.js",
                "/app/views/definitions/tenant/view.units.ui.js",
                "/app/views/definitions/tenant/view.units.logic.js",
            ]);
            break;
        case menu.K_MENU_MEASUREMENT_TYPES:
            await  UtilesForm.loadScripts([
                "/app/views/definitions/base/popup.select.ui.js",
                "/app/views/definitions/base/popup.select.logic.js",
                "/app/views/definitions/base/view.consulta.base.ui.js",
                "/app/views/definitions/base/view.consulta.base.logic.js",
                "/app/views/definitions/tenant/view.measurement_types.ui.js",
                "/app/views/definitions/tenant/view.measurement_types.logic.js"
            ]);
        case menu.K_MENU_PATIENTS:
            await  UtilesForm.loadScripts([
                "/app/views/dashboards/tenant/dashboard.evolution.ui.js",
                "/app/views/dashboards/tenant/dashboard.evolution.logic.js",
                "/app/views/definitions/base/view.consulta.base.ui.js",
                "/app/views/definitions/base/view.consulta.base.logic.js",
                "/app/views/definitions/tenant/view.patient.ui.js",
                "/app/views/definitions/tenant/view.patient.logic.js"
            ]);
            break;
        case menu.K_MENU_APPOINTMENTS:
            await  UtilesForm.loadScripts([
                "/app/views/definitions/base/popup.select.ui.js",
                "/app/views/definitions/base/popup.select.logic.js",
                "/app/views/editors/tenant/editor.appointment.ui.js",
                "/app/views/editors/tenant/editor.appointment.logic.js",
                "/app/views/editors/tenant/editor.measurement.ui.js",
                "/app/views/editors/tenant/editor.measurement.logic.js",
                "/app/views/lists/base/list.base.ui.js",
                "/app/views/lists/base/list.base.logic.js",
                "/app/views/lists/tenant/list.appointments.ui.js",
                "/app/views/lists/tenant/list.appointments.logic.js"
            ]);
            break;
        case menu.K_MENU_MEASUREMENT_REMOTE:
            await  UtilesForm.loadScripts([
                "/app/views/definitions/base/popup.select.ui.js",
                "/app/views/definitions/base/popup.select.logic.js",
                "/app/views/editors/tenant/editor.appointment.ui.js",
                "/app/views/editors/tenant/editor.appointment.logic.js",
                "/app/views/editors/tenant/editor.measurement.ui.js",
                "/app/views/editors/tenant/editor.measurement.logic.js",
                "/app/views/lists/base/list.base.ui.js",
                "/app/views/lists/base/list.base.logic.js",
                "/app/views/lists/tenant/list.remote.appointments.ui.js",
                "/app/views/lists/tenant/list.remote.appointments.logic.js"
            ]);
            break;
        default:
            break;
    }
};

UtilesForm.setupView = async function(sidebar, content, id, initializer) {
    const cellId = id + "Cell";

    if (!content.getChildViews().find(v => v.config.id === cellId)) {
        let newView;
        await UtilesForm.loadScriptsView(id);

        switch(id) {
            case menu.K_MENU_DASHBOARD:
                if (typeof Dashboard !== "undefined" && typeof Dashboard.registerInitializer === "function") {
                    Dashboard.registerInitializer();
                }
                newView = { id: cellId, rows: [ DashboardUI() ]};
                break;
            case menu.K_MENU_UNITS:
                if (typeof Units !== "undefined") {
                    Units.register();
                }

                newView = { id: cellId, rows: [ UnitsUI() ]};
                break;
            case menu.K_MENU_MEASUREMENT_TYPES:
                if (typeof MeasurementType !== "undefined") {
                    MeasurementType.register();
                }

                newView = { id: cellId, rows: [ MeasurementTypeUI() ]};
                break;
            case menu.K_MENU_PATIENTS:
                if (typeof Patients !== "undefined") {
                    Patients.register();
                }

                newView = { id: cellId, rows: [ PatientsUI() ]};
                break;
            case menu.K_MENU_APPOINTMENTS:
                if (typeof ListAppointments !== "undefined") {
                    ListAppointments.register();
                }
                newView = { id: cellId, rows: [ ListAppointmentsUI() ] };
                break;
            case menu.K_MENU_MEASUREMENT_REMOTE:
                if (typeof ListRemoteMeasurements !== "undefined") {
                    ListRemoteMeasurements.register();
                }
                newView = { id: cellId, rows: [ ListRemoteMeasurementsUI() ] };
                break;
            default:
                newView = {
                    id: cellId,
                    template: `<h2 style="text-align:center; margin-top:20px;">${this.getItem(id).value}</h2>`
                };
                break;
        }

        content.addView(newView);

        // Aquí garantizamos que los scripts ya están cargados
        if (typeof initializer !== "function") {
            // buscar el initializer **registrado dinámicamente** después de cargar los scripts
            initializer = UtilesForm.initializers[id];
        }

        // ahora sí ejecutamos el initializer
        if (typeof initializer === "function") {
            initializer(cellId);
        }
    }

    content.setValue(cellId);
    sidebar.select(id);
};

UtilesForm.loadScript = function(url) {
    return new Promise((resolve, reject) => {
        const script = document.createElement("script");
        script.src = url;
        script.async = true;
        script.onload = () => resolve(url);
        script.onerror = () => reject(new Error("No se pudo cargar el script: " + url));
        document.body.appendChild(script);
    });
};

UtilesForm.loadScripts = async function(scripts) {
    for (const s of scripts) {
        await UtilesForm.loadScript(s);
    }
};

UtilesForm.showGlobalOverlay = function() {
    if (document.getElementById("globalOverlay")) return; // ya existe
    const overlay = document.createElement("div");
    overlay.id = "globalOverlay";
    overlay.style.position = "fixed";
    overlay.style.top = 0;
    overlay.style.left = 0;
    overlay.style.width = "100%";
    overlay.style.height = "100%";
    overlay.style.background = "rgba(0,0,0,0.4)";
    overlay.style.display = "flex";
    overlay.style.justifyContent = "center";
    overlay.style.alignItems = "center";
    overlay.style.zIndex = 9999;
    overlay.innerHTML = '<span class="loader"></span>';
    document.body.appendChild(overlay);
};

UtilesForm.hideGlobalOverlay = function() {
    const overlay = document.getElementById("globalOverlay");
    if (overlay) overlay.remove();
};