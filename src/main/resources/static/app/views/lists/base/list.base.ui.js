function ListBaseUI(config) {
    // Construimos las filas internas (toolbar + grid)
    const innerRows = [
        // TOOLBAR
        {
            view: "toolbar",
            css: "my_toolbar",
            padding: { left: 10, right: 10 },
            cols: [
                { view: "label", label: config.title || "Listado", width: 250 },
                {},
                { id: config.btnExcel || "btnExcel", view: "icon", css: "webix_icon_btn", icon: "fa-file-excel-o", tooltip: "Exportar a Excel", width: 40 },
                { id: config.btnNew || "btnNew", view: "icon", css: "webix_icon_btn", icon: "fa-plus", tooltip: "Agregar nuevo", width: 40 }
            ]
        }
    ];

    // Si el hijo pasó filtros, lo agregamos (pero la definición de filtros la hace el hijo)
    if (config.filters && config.filters.length > 0) {
        innerRows.push({
            view: "form",
            id: config.formFilters || "formFiltersBase",
            css: "webix_shadow_medium",
            padding: 12,
            gravity: 0.18,
            elements: config.filters
        });
    }

    // GRILLA
    innerRows.push({
        gravity: 1,
        rows: [
            {
                view: "datatable",
                id: config.gridList || "gridListBase",
                select: "row",
                resizeColumn: true,
                scrollX: false,
                autoheight: false,
                pager: config.gridPager || "pagerListBase",
                columns: (config.columns || []).concat([
                    {
                        id: "actions",
                        header: "",
                        width: 100,
                        template: `
                            <span class='webix_icon fa-edit edit-icon' style='cursor:pointer;margin-right:10px;color:#1463fc;'></span>
                            <span class='webix_icon fa-trash delete-icon' style='cursor:pointer;color:#d9534f;'></span>
                        `
                    }
                ]),
                data: [],
                on: {
                    onAfterRender: function() {
                        // refuerzo DOM: asegurar ancho 100% del contenedor
                        try {
                            this.$view.style.width = "100%";
                            const p = this.$view.parentElement;
                            if (p) p.style.width = "100%";
                        } catch (e) { /* ignore */ }
                    }
                }
            },
            {
                view: "pager",
                id: config.gridPager || "pagerListBase",
                size: 20,
                group: 5,
                template: "{common.first()} {common.prev()} {common.pages()} {common.next()} {common.last()}",
                css: { "text-align": "center" },
                gravity: 0
            }
        ]
    });

    // **WRAPPER cols** para forzar que el componente ocupe todo el ancho del padre
    return {
        cols: [
            {
                view: "form",
                id: config.formList || "formListBase",
                padding: 5,
                rows: innerRows,
                gravity: 1,
                on: {
                    onAfterRender: function() {
                        // defensa extra: forzar estilos del DOM del contenedor
                        try {
                            const el = this.$view;
                            if (el) {
                                el.style.width = "100%";
                                if (el.parentElement) el.parentElement.style.width = "100%";
                            }
                        } catch (e) {}
                    }
                }
            }
        ]
    };
}
