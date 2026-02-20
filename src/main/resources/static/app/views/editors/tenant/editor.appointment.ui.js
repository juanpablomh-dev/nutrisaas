// editor.appointment.ui.js

window.updateNotesCounter = function(noteValue, contextView) {
  const val = noteValue || "";
  const remaining = 1000 - val.length;

  // buscar el view del contador desde el contexto (form o textarea)
  let counter = null;
  try {
    if (contextView && typeof contextView.queryView === "function") {
      counter = contextView.queryView({ id: "notesCounter_00010" });
    }
  } catch (e) {}

  if (!counter && window.$$) {
    counter = $$("notesCounter_00010");
  }
  if (!counter) return;

  // actualizar texto
  counter.setHTML(`${remaining} caracteres restantes`);

  // manipular clases sobre el nodo DOM del contador
  const node = (typeof counter.getNode === "function") ? counter.getNode() : counter.$view;
  if (!node) return;
  node.classList.remove("notes-counter--warning", "notes-counter--error");

  if (remaining <= 0) {
    node.classList.add("notes-counter--error");
  } else if (remaining <= 50) {
    node.classList.add("notes-counter--warning");
  }
};


function EditorAppointmentUI(config) {
    const isNew = !config.data;
    const formHeight = 440;

    return {
        view: "window",
        id: config.id || "windowEditorAppointment_00010",
        position: "center",
        modal: true,
        width: 750,
        height: 600,
        head: {
            view: "toolbar",
            css: "my_toolbar",
            cols: [
                {
                    view: "label",
                    label: isNew ? "Nueva" : "Modificar",
                    align: "left",
                    css: { "font-weight": "bold" }
                },
                {},
                {
                    view: "icon",
                    icon: "fa-close",
                    tooltip: "Cerrar",
                    click: function () {
                        this.getTopParentView().close();
                    }
                }
            ]
        },
        body: {
            rows: [
                {
                    view: "tabview",
                    id: "tabAppointmentEditor_00010",
                    cells: [
                        // PESTAÑA 1: DATOS GENERALES
                        {
                            header: "Datos Generales",
                            width: 140,
                            body: {
                                view: "form",
                                id: config.formId || "formEditorAppointment_00010",
                                scroll: true,
                                padding: 15,
                                height: formHeight,
                                elementsConfig: { labelWidth: 120 },

                                on: {
                                  onAfterRender: function() {
                                    try {
                                      const vals = this.getValues();
                                      const notes = (vals && vals.notes_00010) ? vals.notes_00010 : "";
                                      if (typeof window.updateNotesCounter === "function") {
                                        window.updateNotesCounter(notes, this);
                                      }
                                    } catch (e) { /* ignore */ }
                                  }
                                },

                                elements: [
                                    {
                                        cols: [
                                            {
                                              view: "datepicker",
                                              name: "startDateTime_00010",
                                              label: "Fecha y Hora:",
                                              format: "%d/%m/%Y %H:%i",
                                              stringResult: true,
                                              timepicker: true,
                                              required: true,
                                              width: 335
                                            },
                                            {}
                                         ]
                                    },

                                    {
                                        cols: [
                                            {
                                                view: "text",
                                                id: "patientId_00010",
                                                name: "patientId_00010",
                                                label: "Paciente:",
                                                labelWidth: 120,
                                                invalidMessage: "Debe ingresar un Paciente",
                                                attributes: { maxlength: 20 },
                                                gravity: 2,
                                                required: true
                                            },
                                            {
                                                view: "icon",
                                                icon: "fa-search",
                                                tooltip: "Seleccionar Paciente",
                                                width: 40,
                                                align: "right",
                                                id: "btnSelectPatient_00010"
                                            },
                                            {
                                                view: "label",
                                                name: "patientName_00010",
                                                id: "patientName_00010",
                                                label: "",
                                                css: { "font-weight": "bold", "margin-left": "8px" },
                                                value: "",
                                                gravity: 2,
                                                height: 38
                                            }
                                        ]
                                    },

                                    {
                                        cols: [
                                            {
                                                view: "combo",
                                                name: "status_00010",
                                                label: "Estado:",
                                                width: 335,
                                                options: [
                                                    { id: "PENDING", value: "Pendiente" },
                                                    { id: "COMPLETED", value: "Realizado" },
                                                    { id: "CANCELLED", value: "Cancelado" }
                                                ],
                                                value: "PENDING"
                                            },
                                            {}
                                         ]
                                    },

                                    // TEXTAREA with onTimedKeyPress + onChange
                                    {
                                        cols: [
                                            {
                                              view: "textarea",
                                              name: "notes_00010",
                                              id: "notes_00010",
                                              label: "Notas:",
                                              height: 100,
                                              attributes: { maxlength: 1000 },
                                              on: {
                                                onTimedKeyPress: function () {
                                                  const valRaw = this.getValue() || "";
                                                  const val = valRaw.length > 1000 ? valRaw.substring(0,1000) : valRaw;
                                                  if (val !== valRaw) this.setValue(val);
                                                  if (typeof window.updateNotesCounter === "function") {
                                                    // pasar el form como contexto para que la función encuentre el contador
                                                    const form = (typeof this.getTopParentView === "function") ? this.getTopParentView() : null;
                                                    window.updateNotesCounter(val, form);
                                                  }
                                                },
                                                onChange: function (newv, oldv) {
                                                  const val = (newv || "").substring(0,1000);
                                                  if (newv !== val) this.setValue(val);
                                                  if (typeof window.updateNotesCounter === "function") {
                                                    const form = (typeof this.getTopParentView === "function") ? this.getTopParentView() : null;
                                                    window.updateNotesCounter(val, form);
                                                  }
                                                }
                                              }
                                            }
                                         ]
                                    },

                                    // contador bajo el textarea
                                    {
                                        cols: [
                                            {},
                                            {
                                              view: "template",
                                              id: "notesCounter_00010",
                                              template: "1000 caracteres restantes",
                                              height: 22,
                                              css: "notes-counter",
                                              borderless: true
                                            }
                                         ]
                                    }
                                ]
                            }
                        },

                        // PESTAÑA 2: MEDICIONES
                        {
                            header: "Mediciones",
                            width: 120,
                            body: {
                                height: formHeight,
                                rows: [
                                    {
                                        cols: [
                                            {},
                                            {
                                              view: "button",
                                              id: "btnAddMeasurement_00010",
                                              value: "Agregar medida",
                                              css: "webix_primary",
                                              width: 160,
                                              icon: "fa-heartbeat"
                                            }
                                        ],
                                        padding: 5,
                                        height: 50
                                    },
                                    {
                                        view: "datatable",
                                        id: "gridMeasurements_00010",
                                        select: "row",
                                        editable: false,
                                        //fillspace: true,
                                        columns: [
                                            { id: "typeId", header: "", hidden: true},
                                            { id: "typeName", header: "Tipo de Medida", fillspace: true },
                                            {
                                                id: "value",
                                                header: "Valor",
                                                width: 120,
                                                editor: "text",
                                                format: webix.Number.format,
                                                template: obj => `<div style="text-align:right">${webix.Number.format(obj.value)}</div>`
                                            },
                                            { id: "unitId", header: "", hidden: true },
                                            { id: "unitSymbol", header: "Unidad", width: 80 },
                                            {
                                                id: "actions",
                                                header: "",
                                                width: 80,
                                                template: `
                                                    <span class='webix_icon fa-trash delete-icon' style='cursor:pointer;color:#d9534f;padding-top: 10px;'></span>
                                                    `
                                            }
                                        ],
                                        onClick: {
                                            "delete-icon": function (e, id) {
                                                this.remove(id);
                                                return false;
                                            }
                                        },
                                        data: []
                                    }
                                ]
                            }
                        }
                    ]
                },

                // toolbar inferior
                {
                    view: "toolbar",
                    padding: { top: 10, right: 15, bottom: 10, left: 15 },
                    css: "my_toolbar",
                    cols: [
                        {},
                        { view: "button", id: "btnSaveAppointment_00010", value: "Guardar", css: "webix_primary", width: 120 },
                        {
                            view: "button",
                            value: "Cancelar",
                            width: 100,
                            click: function () {
                                this.getTopParentView().close();
                            }
                        }
                    ]
                }
            ]
        }
    };
}
