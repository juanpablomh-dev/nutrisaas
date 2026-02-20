function PopupSelectUI(config) {
    return {
        view: "window",
        id: config.id || "popupSelect",
        width: config.width || 600,
        height: config.height || 600,
        position: "center",
        modal: true,
        head: {
            view: "toolbar",
            cols: [
                { view: "label", label: config.title || "Seleccionar elemento", align: "left" },
                { view: "icon", icon: "wxi-close", click: function() { this.getTopParentView().close(); } }
            ]
        },
        body: {
            rows: [
                {
                    view: "datatable",
                    id: config.gridId || "popupSelectGrid",
                    select: "row",
                    columns: (config.columns || [
                        { id: "id", header: "ID", width: 60 },
                        { id: "name", header: "Nombre", fillspace: true }
                    ]).map(col => {
                        if (col.id === "active") {
                            return {
                                ...col,
                                header: [
                                    col.header,
                                    {
                                        content: "selectFilter",
                                        options: [
                                            { id: 1, value: "Sí" },
                                            { id: 0, value: "No" }
                                        ]
                                    }
                                ],
                                template: (obj) => `
                                    <div style="display:flex; align-items:center; justify-content:center; height:100%;">
                                        <span class="webix_icon ${obj.active ? "fa-check-square" : "fa-square"}"
                                              style="color:${obj.active ? "#1463fc" : "gray"}; font-size:16px;"></span>
                                    </div>`
                            };
                        }

                        return {
                            ...col,
                            header: [col.header, { content: "textFilter" }]
                        };
                    }),
                    autoheight: false,
                    scroll: "y",
                    resizeColumn: true,
                    data: []
                },
                {
                    cols: [
                        {},
                        {
                            view: "button",
                            value: "Seleccionar",
                            css: "webix_primary",
                            width: 120,
                            id: config.btnSelectId || "btnSelect"
                        },
                        {
                            view: "button",
                            value: "Cancelar",
                            width: 100,
                            click: function() {
                                this.getTopParentView().close();
                            }
                        }
                    ]
                }
            ]
        }
    };
}
