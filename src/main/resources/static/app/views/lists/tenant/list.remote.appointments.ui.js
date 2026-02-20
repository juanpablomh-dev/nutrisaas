function ListRemoteMeasurementsUI() {
    return ListBaseUI({
        formList: "formListAppointmentsRemote",
        formFilters: "formFilterAppointmentsRemote",
        gridList: "gridAppointmentsRemote",
        gridPager: "pagerAppointmentsRemote",
        btnExcel: "btnExcelAppointmentsRemote",
        btnNew: "btnNewAppointmentsRemote",
        btnSearch: "btnSearchAppointmentsRemote",
        title: "Mediciones no Presenciales",

        filters: [
            {
                cols: [
                    {
                        rows: [
                            {
                                view: "datepicker",
                                name: "fromDateRemote",
                                id: "fromDateRemote",
                                label: "Desde:",
                                format: "%d/%m/%Y %H:%i",
                                stringResult: true,
                                timepicker: true
                            },
                            {
                                view: "datepicker",
                                name: "toDateRemote",
                                id: "toDateRemote",
                                label: "Hasta:",
                                format: "%d/%m/%Y %H:%i",
                                stringResult: true,
                                timepicker: true
                            }
                        ],
                        gravity: 0.4
                    },
                    {gravity: 0.1},
                    {
                        rows: [
                            {
                                cols: [
                                    {
                                        view: "text",
                                        name: "patientIdRemote",
                                        id: "patientIdRemote",
                                        label: "Paciente:",
                                        labelWidth: 90
                                    },
                                    {
                                        view: "icon",
                                        id: "btnSelectPatientRemote",
                                        icon: "fa-search",
                                        tooltip: "Seleccionar paciente",
                                        width: 40
                                    },
                                    {
                                        view: "label",
                                        name: "patientNameRemote",
                                        id: "patientNameRemote",
                                        label: "",
                                        css: { "font-weight": "bold", "margin-left": "8px" },
                                        value: "",
                                        height: 38
                                    }
                                ]
                            },
                            {
                                view: "combo",
                                name: "statusRemote",
                                id: "statusRemote",
                                label: "Estado:",
                                labelWidth: 90,
                                options: [
                                    { id: "ALL", value: "Todos" },
                                    { id: "PENDING", value: "Pendiente" },
                                    { id: "COMPLETED", value: "Completados" },
                                    { id: "CANCELLED", value: "Cancelados" }
                                ]
                            }
                        ],
                        gravity: 0.5
                    },
                    {},
                    {
                        rows: [
                            {
                                cols: [

                                ]
                            },
                            {
                                view: "button",
                                id: "btnSearchAppointmentsRemote",
                                value: "Buscar",
                                css: "webix_primary",
                                width: 120,
                                align: "center",
                                gravity: 0.22
                            }
                        ],
                        gravity: 0.6
                    }
                ]
            }
        ],

        columns: [
            { id: "id", header: "ID", width: 60 },
            { id: "date", header: "Fecha", width: 140, format: webix.Date.dateToStr("%d/%m/%y") },
            { id: "time", header: "Hora", width: 100 },
            { id: "patientId", hidden: true },
            { id: "patientName", header: "Paciente", fillspace: true },
            {
                id: "status",
                header: "Estado",
                width: 140,
                template: function(obj) {
                    const map = {
                        PENDING:   { text: "Pendiente",  cls: "status-pending" },
                        COMPLETED: { text: "Completado",  cls: "status-completed" },
                        CANCELLED: { text: "Cancelado",  cls: "status-cancelled" }
                    };

                    const s = map[obj.status] || { text: obj.status || "—", cls: "status-default" };
                    // Añadimos aria-label por accesibilidad
                    return `<div class="status-tag ${s.cls}" role="status" aria-label="${s.text}">${s.text}</div>`;
                }
            }
        ]
    });
}
