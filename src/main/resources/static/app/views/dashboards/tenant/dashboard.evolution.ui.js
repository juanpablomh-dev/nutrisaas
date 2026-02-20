// dashboard.evolution.ui.js
// UI del módulo "Evolución de Métricas del Paciente" - versión comparador 2-columnas

class DashboardEvolutionUI {

    static WINDOW_ID_PREFIX = "win_dashboard_evolution_";

    /**
     * Construye la UI del dashboard Evolution para un paciente
     */
    static getUIConfig(patientId, patientName, patientAge) {

        // helper para construir una columna (A o B)
        const columnFor = (side) => {
            const suf = `_${side}`;
            return {
                rows: [
                    // selector
                    {
                        padding: 10,
                        cols: [
                            { width: 80, view: "label", label: "Métrica" },
                            {
                                view: "combo",
                                id: `evoMetricSelector_${patientId}${suf}`,
                                options: [],
                                placeholder: "Seleccione una métrica…",
                                on: {
                                    onChange: function (metric) {
                                        DashboardEvolutionLogic.instances[patientId]?.updateMetric(metric, side);
                                    }
                                }
                            }
                        ]
                    },

                    // chart
                    {
                        view: "chart",
                        id: `evoChart_${patientId}${suf}`,
                        type: "line",
                        value: "#value#",
                        x: "#date#",
                        height: 300,
                        tooltip: { template: "#date#: #value# #unit#" }
                    },

                    // summary
                    {
                        view: "template",
                        id: `evoSummary_${patientId}${suf}`,
                        height: 80,
                        template: "<div style='padding:10px;'>Cargando…</div>"
                    },

                    // table
                    {
                        view: "datatable",
                        id: `evoTable_${patientId}${suf}`,
                        autoheight: false,
                        height: 300,
                        columns: [
                            { id: "date", header: "Fecha", width: 130 },
                            { id: "value", header: "Valor", fillspace: true },
                            { id: "unit", header: "Unidad", width: 90 }
                        ],
                        data: []
                    }
                ]
            };
        };

        return {
            id: `evoRoot_${patientId}`,
            rows: [
                // HEADER
                {
                    view: "toolbar",
                    css: "my_toolbar",
                    cols: [
                        {
                            view: "label",
                            label:
                                "Evolución del Paciente"
                                + (patientName ? " — " + patientName : "")
                                + (patientAge ? " (" + patientAge + " años)" : ""),
                            css: "bold"
                        },
                        {},
                        { view: "button", id: `btnEvolutionExport_${patientId}`, value: "Exportar PDF", css: "webix_primary", width: 150 },
                        {
                            view: "icon",
                            icon: "fa-times",
                            tooltip: "Cerrar",
                            click: function () {
                                const win = this.getTopParentView();
                                win.close();
                            }
                        }
                    ]
                },
                {
                    view: "tabview",
                    id: "tabDashboard_00010",
                    cells: [
                        // PESTAÑA 1: Evolución
                        {
                            header: "Evolución",
                            width: 120,
                            body: {
                                rows: [
                                    {
                                        cols: [
                                            {
                                                cols: [
                                                    { width: 10 },
                                                    {
                                                        // Left column (A)
                                                        width: 540,
                                                        id: `evoPanelA_${patientId}`,
                                                        rows: [
                                                            // header label for left
                                                            { view: "template", template: "<div style='padding:6px 10px;font-weight:600;'>Panel A</div>", height: 30, borderless: true },
                                                            columnFor("A")
                                                        ]
                                                    },
                                                    { width: 10 },
                                                    {
                                                        // Right column (B)
                                                        id: `evoPanelB_${patientId}`,
                                                        width: 540,
                                                        rows: [
                                                            // header label for right
                                                            { view: "template", template: "<div style='padding:6px 10px;font-weight:600;'>Panel B</div>", height: 30, borderless: true },
                                                            columnFor("B")
                                                        ]
                                                    },
                                                    { width: 20 }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            }
                        },

                        // PESTAÑA 2: Velocidad
                        {
                            header: "Velocidad",
                            width: 120,
                            body: {
                                rows: [
                                    {
                                        cols: [
                                            {
                                                rows: [
                                                    // HEADER / CONTROLES
                                                    {
                                                        padding: 10,
                                                        cols: [
                                                            { width: 80, view: "label", label: "Métrica" },
                                                            {
                                                                view: "combo",
                                                                id: `evoPaceMetric_${patientId}`,
                                                                placeholder: "Seleccione una métrica…",
                                                                options: []
                                                            },
                                                            { width: 80 },
                                                            {
                                                                view: "template",
                                                                id: `evoPaceSummary_${patientId}`,
                                                                template: "<div style='padding:6px;'>Seleccione una métrica para ver la velocidad</div>",
                                                                borderless: true
                                                            }
                                                        ]
                                                    },
                                                    // GRILLA DE VELOCIDADES
                                                    {
                                                        view: "datatable",
                                                        id: `evoPaceTable_${patientId}`,
                                                        autoheight: false,
                                                        height: 600,
                                                        resizeColumn: true,
                                                        columns: [
                                                            {
                                                                id: "fromDate",
                                                                header: "Desde",
                                                                width: 130
                                                            },
                                                            {
                                                                id: "toDate",
                                                                header: "Hasta",
                                                                width: 130
                                                            },
                                                            {
                                                                id: "deltaValue",
                                                                header: { text: "Δ Valor", css: { "text-align": "right" } },
                                                                width: 120,
                                                                format: webix.Number.format({ decimalSize: 2 }),
                                                                css: { "text-align": "right" }
                                                            },
                                                            {
                                                                id: "deltaDays",
                                                                header: { text: "Δ Días", css: { "text-align": "right" } },
                                                                width: 90,
                                                                css: { "text-align": "right" }
                                                            },
                                                            {
                                                                id: "pace",
                                                                header: { text: "Velocidad", css: { "text-align": "right" } },
                                                                fillspace: true,
                                                                format: webix.Number.format({ decimalSize: 3 }),
                                                                css: { "text-align": "right" }
                                                            },
                                                            {
                                                                id: "unit",
                                                                header: "Unidad",
                                                                width: 100
                                                            }
                                                        ],
                                                        data: []
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            }
                        }
                    ]
                },
                { height: 10 }
            ]
        };
    }

    static open(patientId, patientName, patientAge) {
        const winId = this.WINDOW_ID_PREFIX + patientId;

        if ($$(winId)) $$(winId).close();

        // 1) Crear la ventana
        webix.ui({
            view: "window",
            id: winId,
            width: 1100,
            height: 900,
            position: "center",
            modal: true,
            head: false,
            body: this.getUIConfig(patientId, patientName, patientAge),
            on: {
                onShow: function () {
                   const btn = $$(`btnEvolutionExport_${patientId}`);
                   if (!btn) return;

                   btn.attachEvent("onItemClick", async () => {

                       const { jsPDF } = window.jspdf;

                       // CONFIGURACIÓN GLOBAL
                       const MARGIN = 10;     // mm
                       const PAGE_W = 210;    // A4
                       const PAGE_H = 297;    // A4
                       const ZOOM = 1.4;      // aumentar o reducir el tamaño de la captura

                       const pdf = new jsPDF({
                           unit: "mm",
                           format: "a4",
                           orientation: "portrait"
                       });

                       // Función auxiliar para capturar un panel
                       const capturePanel = async (viewId) => {
                           const view = $$(viewId);
                           if (!view) return null;

                           const node = view.$view;

                           const canvas = await html2canvas(node, {
                               scale: 2 * ZOOM,
                               useCORS: true
                           });

                           return canvas.toDataURL("image/png");
                       };

                       // ------------------ PANEL A ------------------
                       const imgA = await capturePanel(`evoPanelA_${patientId}`);
                       if (imgA) {
                           const imgProps = pdf.getImageProperties(imgA);

                           const maxW = PAGE_W - MARGIN * 2;
                           const newH = (imgProps.height * maxW) / imgProps.width;

                           pdf.addImage(imgA, "PNG", MARGIN, MARGIN, maxW, newH);
                       }

                       // ------------------ PANEL B ------------------
                       const imgB = await capturePanel(`evoPanelB_${patientId}`);
                       if (imgB) {
                           pdf.addPage();

                           const imgProps = pdf.getImageProperties(imgB);

                           const maxW = PAGE_W - MARGIN * 2;
                           const newH = (imgProps.height * maxW) / imgProps.width;

                           pdf.addImage(imgB, "PNG", MARGIN, MARGIN, maxW, newH);
                       }

                       // Descargar archivo final
                       pdf.save(`evolucion_${patientId}.pdf`);
                   });


                }
            }
        }).show();

        // 2) Inicializar la lógica
        DashboardEvolutionLogic.instances[patientId] = new DashboardEvolutionLogic(patientId);
        DashboardEvolutionLogic.instances[patientId].loadAll();
    }

}
