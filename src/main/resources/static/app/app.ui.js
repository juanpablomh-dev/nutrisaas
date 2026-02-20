function AppUI() {
    var menu_data_multi  = [
        {id: "dashboard", icon: "mdi mdi-book", value:"Dashboard"},
        {id: "definiciones", icon: "mdi mdi-pencil", value:"Definiciones", data:[
            { id: "units", icon:"mdi mdi-circle", value: "Unidades"},
            { id: "measurement_types", icon:"mdi mdi-circle", value: "Tipos de Medida"},
            { id: "patients", icon:"mdi mdi-circle", value: "Pacientes"},
        ]},
        {id: "appointments", icon: "mdi mdi-calendar", value:"Citas"},
        {id: "measurement_remote", icon: "mdi mdi-laptop", value:"Medición no Presencial"},
    ];
    return {
       rows: [
           // Toolbar
           {
               view: "toolbar", height: 50, padding: 3,
               cols: [
                   { view: "icon", icon: "mdi mdi-menu", click: function(){$$("sidebar").toggle();}},
				   { template: "<img src='../../images/logo.png' style='height:40px'>", width: 60, borderless: true },
                   { view: "label", label: "NutriSaaS", width: 180 },
                   {},
                   { view: "icon", icon: "mdi mdi-comment", badge: 4 },
                   { view: "icon", icon: "mdi mdi-bell", badge: 10 },
				   { view: "button", value: "Salir", width: 100, id: "logoutBtn", css: "webix_danger" }
               ]
           },
           // Contenedor principal
           {
               cols: [
                   { view: "sidebar", id: "sidebar", width: 300, data: menu_data_multi},
                   { view: "multiview", id: "content", gravity: 1, cells: [{ id: "empty", template: "" }] }
               ],
               gravity: 1
           }
       ]
   };
}
