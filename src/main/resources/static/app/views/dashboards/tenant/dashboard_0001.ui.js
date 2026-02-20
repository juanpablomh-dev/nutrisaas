// UI definition para el Dashboard del paciente (encapsulado en Dashboard0001)

class Dashboard0001 {

    // id del tabview principal donde se crearán las pestañas (ajusta si en tu app es distinto)
    static MAIN_TABVIEW_ID = "mainTabview";

    // prefijo para la pestaña / panel del paciente
    static TAB_ID_PREFIX = "tab_dashboard_patient_";

    // id interno del contenido del dashboard (dentro de la pestaña)
    static CONTENT_ID_PREFIX = "dashboard_content_";

    static getUIConfig(patientId, patientName, withoutHeader = false) {
        const contentId = Dashboard0001.CONTENT_ID_PREFIX + patientId;

        const headerToolbar = {
            view: "toolbar",
            css: "my_toolbar",
            cols: [
                { view: "label", label: `Dashboard — ${patientName || ("Paciente " + patientId)}`,
                  css: { "font-weight": "bold" } },
                {},
                { view: "button", id: `btnExportPdf_${patientId}`, value: "Exportar PDF",
                  width: 120, css: "webix_primary" },
                { view: "icon", icon: "fa-times", tooltip: "Cerrar pestaña", click: function() {
                    const tabId = Dashboard0001.TAB_ID_PREFIX + patientId;
                    const main = $$(Dashboard0001.MAIN_TABVIEW_ID);
                    if (main && typeof main.removeView === "function") {
                        main.removeView(tabId);
                    }
                }}
            ]
        };

        return {
            id: Dashboard0001.TAB_ID_PREFIX + patientId + "_content",
            rows: [
                ...(withoutHeader ? [] : [headerToolbar]),
                {
                    cols: [
                        {
                            width: 360,
                            rows: [
                                {
                                    view: "template",
                                    id: `patientHeader_${patientId}`,
                                    template: `<div style="padding:10px;">
                                        <div style="font-size:18px;font-weight:700;">${patientName || ""}</div>
                                        <div id="patientMeta_${patientId}">Cargando datos...</div>
                                    </div>`,
                                    height: 80,
                                    borderless: true
                                },
                                {
                                    view: "dataview",
                                    id: `kpiCards_${patientId}`,
                                    css: "kpi-container",
                                    template: `
                                        <div class='kpi-card'>
                                            <div class='kpi-title'>#name#</div>
                                            <div class='kpi-value'>#value#</div>
                                            <div class='kpi-change #changeColor#'>#change#</div>
                                        </div>`,
                                    type: { height: 90, width: 320 },
                                    scroll: "auto",
                                    data: []
                                }
                            ]
                        },
                        //  GRAFICAS
                        {
                            fillspace: true,
                            rows: [
                                {
                                    cols: [
                                        {
                                            view: "chart",
                                            id: `chartWeight_${patientId}`,
                                            type: "line",
                                            value: "#y#",
                                            x: "#x#",
                                            legend: false,
                                            height: 220,
                                            tooltip: { template: "#x# -> #y#" }
                                        },
                                        {
                                            view: "chart",
                                            id: `chartBMI_${patientId}`,
                                            type: "line",
                                            value: "#y#",
                                            x: "#x#",
                                            height: 220,
                                            tooltip: { template: "#x# -> #y#" }
                                        }
                                    ]
                                },
                                {
                                    cols: [
                                        {
                                            view: "chart",
                                            id: `chartFat_${patientId}`,
                                            type: "area",
                                            value: "#y#",
                                            x: "#x#",
                                            height: 220,
                                            tooltip: { template: "#x# -> #y#" }
                                        },
                                        {
                                            view: "chart",
                                            id: `chartWHR_${patientId}`,
                                            type: "line",
                                            value: "#y#",
                                            x: "#x#",
                                            height: 220,
                                            tooltip: { template: "#x# -> #y#" }
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    cols: [
                        // GRILLA MÉTRICA
                        {
                            view: "datatable",
                            id: `compareTable_${patientId}`,
                            columns: [
                                { id: "metric", header: "Métrica", fillspace: 2 },
                                { id: "last", header: "Última", width: 120, css: "webix_right" },
                                { id: "prev", header: "Anterior", width: 120, css: "webix_right" },
                                { id: "delta", header: "Δ", width: 100, css: "webix_right" },
                                { id: "percent", header: "%Δ", width: 100, css: "webix_right" }
                            ],
                            height: 260,
                            data: []
                        },
                        // RECOMENDACIONES
                        {
                            view: "template",
                            id: `recommendations_${patientId}`,
                            template: "<div style='padding:10px;'><h4>Recomendaciones</h4><div id='recContent'>Cargando...</div></div>",
                            width: 420,
                            borderless: true
                        }
                    ]
                }
            ]
        };
    }

    static openInTab(patientId, patientName) {
        const main = $$(Dashboard0001.MAIN_TABVIEW_ID);

        // construimos id de tab
        const tabId = Dashboard0001.TAB_ID_PREFIX + patientId;
        const contentId = Dashboard0001.CONTENT_ID_PREFIX + patientId;

        // si ya existe la pestaña, la mostramos
        if (main && main.getChildView && main.getChildView(tabId)) {
            main.getChildView(tabId).show();
            return;
        }

        // si existe el main tabview, añadimos una nueva pestaña
        if (main && typeof main.addView === "function") {
            const tab = {
                id: tabId,
                header: `Dashboard ${patientId}`,
                body: {
                    id: tabId + "_body",
                    rows: [
                        // contenido del dashboard
                        Dashboard0001.getUIConfig(patientId, patientName)
                    ]
                }
            };

            // si main es un tabview tipo 'tabview' con addTab / addView
            try {
                // intentamos crear una pestaña usando addView
                main.addView({
                    id: tabId,
                    header: `Resultados: ${patientName || patientId}`,
                    body: Dashboard0001.getUIConfig(patientId, patientName)
                });
                main.show(tabId);
            } catch (e) {
                // fallback: si addView no funciona, intentamos addTab (otro tipo de tabview)
                if (typeof main.addTab === "function") {
                    const newTab = main.addTab(`Resultados: ${patientName || patientId}`);
                    main.getChildView(newTab).addView(Dashboard0001.getUIConfig(patientId, patientName));
                    main.show(newTab);
                } else {
                    // fallback final: crear ventana modal
                    Dashboard0001.openInWindow(patientId, patientName);
                }
            }

            // attach export button handler (delegado por timeout para que exista el DOM)
            setTimeout(() => {
                const btnExport = $(`btnExportPdf_${patientId}`);
                if (btnExport && typeof btnExport.attachEvent === "function") {
                    btnExport.attachEvent("onItemClick", () => {
                        Dashboard0001.exportPDF(tabId + "_body");
                    });
                } else {
                    const btn = $$(`btnExportPdf_${patientId}`);
                    if (btn) btn.attachEvent("onItemClick", () => Dashboard0001.exportPDF(tabId + "_body"));
                }
            }, 300);

            // iniciar carga de datos
            const logic = new Dashboard0001Logic(patientId);
            logic.loadDashboardData();

            return;
        }

        // si no existe main tabview, abrir en ventana modal
        Dashboard0001.openInWindow(patientId, patientName);
    }

    static openInWindow(patientId, patientName) {
        const winId = Dashboard0001.TAB_ID_PREFIX + patientId;
        if ($$(winId)) $$(winId).close();

        webix.ui({
            view: "window",
            id: winId,
            position: "center",
            modal: true,
            width: 1300,
            height: 900,
            head: {
                view: "toolbar",
                css: "my_toolbar",
                cols: [
                    { view: "label", label: `Dashboard — ${patientName || patientId}`, css: { "font-weight": "bold" } },
                    {},
                    { view: "button", value: "Exportar PDF", width: 140, css: "webix_primary", id: `btnExportPdf_${patientId}` },
                    { view: "icon", icon: "fa-close", tooltip: "Cerrar", click: function () { this.getTopParentView().close(); } }
                ]
            },
            body: Dashboard0001.getUIConfig(patientId, patientName, true) // <== SIN HEADER INTERNO
        }).show();

        $$(`btnExportPdf_${patientId}`).attachEvent("onItemClick", () => Dashboard0001.exportPDF(winId));

        const logic = new Dashboard0001Logic(patientId);
        logic.loadDashboardData();
    }

    static exportPDF(containerId) {
        // intenta exportar el componente principal de la pestaña/ventana
        try {
            const root = $$(containerId);
            if (root) {
                webix.toPDF(root);
                return;
            }
        } catch (e) {
            console.warn("PDF export fallback", e);
        }
        // fallback: captura DOM y usar webix.pdf or alert
        webix.alert("Exportar a PDF no está disponible en este entorno.");
    }
}
