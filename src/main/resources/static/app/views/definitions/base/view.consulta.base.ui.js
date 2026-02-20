function ViewConsultaBaseUI(config) {
    return {
        view: "form",
        id: config.formConsult || "formConsult",
        padding: 5,
        elements: [
            {
                cols: [
                    {
                        view: "label",
                        label: config.title || "Consulta",
                        css: "custom_title_header",
                        align: "left"
                    },
                    {},
                    {
                        id: config.btnExcel || "btnExcel",
                        view: "icon",
                        css: "webix_icon_btn",
                        icon: "fa-file-excel-o",
                        tooltip: "Exportar a Excel",
                        width: 40
                    },
                    {
                        id: config.btnNew || "btnNew",
                        view: "icon",
                        css: "webix_icon_btn",
                        icon: "fa-plus",
                        tooltip: "Agregar nuevo",
                        width: 40
                    }
                ]
            },
            {
                cols: [
                    {
                        gravity: 3,
                        rows: [
                            {
                                view: "datatable",
                                id: config.gridConsult || "gridConsult",
                                select: "row",
                                scrollX: false,
                                scrollY: true,
                                pager: config.gridPager || "gridPager",
                                columns: (config.columns || []).concat([
                                    {
                                        id: "delete",
                                        header: "",
                                        width: 50,
                                        template: "<span class='webix_icon fa-trash' style='cursor:pointer; display:flex; align-items:center; justify-content:center; height:100%;'></span>",
                                        tooltip: "Eliminar registro",
                                        hidden: config.hiddenDelete || false,
                                    }
                                ]),
                                data: []
                            },
                            {
                                view: "layout",
                                height: 50,
                                borderless: false,
                                css: "webix_segment",
                                cols: [
                                    {
                                        // ID dinámico del contenedor
                                        id: (config.gridPager + "_container"),
                                        borderless: false,
                                        css: "webix_segment",
                                        padding: { left: 10, right: 10, top: 5, bottom: 5 },

                                        // Wrapper único para cada pager
                                        template: function () {
                                            const wrapperId = `${config.gridPager || "gridPager"}_wrapper`;
                                            return `<div id='${wrapperId}' style='display:flex; justify-content:center; align-items:center; height:100%; width:100%;'></div>`;
                                        },

                                        on: {
                                            onAfterRender: function () {
                                                const pagerId = config.gridPager || "gridPager";
                                                const wrapperId = `${pagerId}_wrapper`;

                                                // Destruir pager anterior si existe
                                                if ($$(pagerId)) {
                                                    try { $$(pagerId).destructor(); } catch (e) { console.warn("Pager cleanup:", e); }
                                                }

                                                // Crear nuevo pager sólo si el wrapper existe en el DOM
                                                const wrapper = document.getElementById(wrapperId);
                                                if (wrapper) {
                                                    webix.ui({
                                                        view: "pager",
                                                        id: pagerId,
                                                        size: 20,
                                                        group: 5,
                                                        css: { "text-align": "center" },
                                                        template: "{common.first()} {common.prev()} {common.pages()} {common.next()} {common.last()}"
                                                    }, wrapper);
                                                } else {
                                                    console.warn("Pager wrapper not found:", wrapperId);
                                                }
                                            }
                                        }
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        gravity: 3,
                        header: "General",
                        body: {
                            view: "form",
                            id: config.formDetail || "formDetail",
                            scroll: false,
                            rules: config.rules || {},
                            //elementsConfig: config.elementsConfig || {},
                            elementsConfig: {
                                invalidMessage: "Campo inválido",
                                on: {
                                    onBlur: function () {
                                        const form = this.getFormView();
                                        if (!form || !this.config || !this.config.name) return;

                                        const fieldName = this.config.name;
                                        const value = this.getValue?.() ?? "";
                                        const trimmed = value.toString().trim();
                                        const node = this.getNode();
                                        if (!node) return;

                                        const hasInvalidClass = node.classList.contains("webix_invalid");

                                        if (hasInvalidClass && trimmed !== "") {
                                            // 1 Quita el borde rojo
                                            node.classList.remove("webix_invalid");

                                            // 2️ Limpia el texto de error (visual)
                                            this.setBottomText("");

                                            // 3️ Limpia el mensaje DOM (webix_inp_bottom_label)
                                            const bottomLabel = node.querySelector(".webix_inp_bottom_label");
                                            if (bottomLabel) bottomLabel.textContent = "";

                                            // 4️ Limpia estado interno
                                            if (form.elements[fieldName]) {
                                                form.elements[fieldName].config.invalid = false;
                                            }
                                        }
                                    }
                                }
                            },
                            gravity: 1,
                            elements: [
                                ...(config.fields || []),
                                {},
                                {
                                    view: "button",
                                    value: "Guardar",
                                    css: "webix_primary",
                                    id: config.btnSave || "btnSave"
                                }
                            ]
                        }
                    }
                ]
            },
            {},
        ]
    };
}
