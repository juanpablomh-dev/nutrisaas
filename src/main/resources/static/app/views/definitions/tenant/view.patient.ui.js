function PatientsUI() {
    return ViewConsultaBaseUI({
        formConsult: "formConsultPatient",
        title: "Pacientes",
        gridConsult: "gridConsultPatient",
        gridPager: "gridPagerPatient",
        formDetail: "formDetailPatient",
        hiddenDelete: true,
        btnSave: "btnSavePatient",
        btnExcel:"btnExcelPatient",
        btnNew:"btnNewPatient",
        columns: [
            { id: "id", header: "ID", width: 40 },
            { id: "firstName", header: "Nombre", width: 170 },
            { id: "lastName", header: "Apellido", fillspace: true },
            { id: "phone", header: "Teléfono", width: 100 },
            { id: "email", hidden: true },
            { id: "birthDate", hidden: true },
            { id: "gender", hidden: true },
            { id: "notes", hidden: true },
            {
                id: "active",
                header: "Activa",
                width: 60,
                adjust: "header",
                css: { "text-align": "center" },
                template: function (obj) {
                    return `<div style="display:flex; align-items:center; justify-content:center; height:100%;">
                                <span class="webix_icon ${obj.active ? "fa-check-square" : "fa-square"}"
                                      style="color:${obj.active ? "#1463fc" : "gray"}; font-size:16px;"></span>
                            </div>`;
                }
            }
        ],
        fields: [
            { view: "text", name: "id", label: "Id:", labelWidth: 100 },
            { view: "text", name: "firstName", label: "Nombre:", labelWidth: 100, invalidMessage: "Dede ingresar un Nombre", attributes: { maxlength: 100 } },
            { view: "text", name: "lastName", label: "Apellido:", labelWidth: 100, invalidMessage: "Dede ingresar un Apellido", attributes: { maxlength: 100 } },
            { view: "text", name: "email", label: "Email:", labelWidth: 100, invalidMessage: "Dede ingresar un email", attributes: { maxlength: 100 } },
            { view: "text", name: "phone", label: "Teléfono:", labelWidth: 100, invalidMessage: "Dede ingresar un teléfono", attributes: { maxlength: 100 } },
            {
                cols: [
                    { view: "local_datepicker", name: "birthDate", id: "birthDate", label: "Fecha Nac.:", labelWidth: 100, invalidMessage: "Debe ingresar una fecha válida", gravity: 3 },
                    { view: "text", name: "age", label: "", gravity: 2, disabled: true}
                ]
            },
            { view: "radio", name: "gender", label: "Género:", labelWidth: 100,
              options: [
                         { id: "UNSPECIFIED", value: "No Especificado" },
                         { id: "FEMALE", value: "Femenino" },
                         { id: "MALE", value: "Masculino" }
                       ], vertical: false, value: "UNSPECIFIED" },
            { view: "textarea", name: "notes", label: "Notas:", labelWidth: 100, height: 150, attributes: { maxlength: 1000 } },
            { view: "switch", name: "active", label: "Activo", labelWidth: 100 }
        ],
        rules:{
            "firstName": webix.rules.isNotEmpty,
            "lastName": webix.rules.isNotEmpty,
            "email": function (value) {
                if (!value) return false;
                const regex = /^[\w.-]+@([\w-]+\.)+[\w-]{2,4}$/;
                return regex.test(value);
            },
            "phone": function (value) {
                if (!value) return false;
                const regex = /^[0-9()+\\-\\s]{6,20}$/;
                return regex.test(value);
            },
            "birthDate": function (value) {
                const date = Utiles.parseDate(value);
                if (!date) return false;

                const today = new Date();
                today.setHours(0, 0, 0, 0);

                return date <= today;
            }
        }
    });
}
