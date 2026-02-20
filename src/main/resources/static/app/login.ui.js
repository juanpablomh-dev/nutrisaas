function LoginUI() {
  return {
             container: "app",
             rows: [
                 {
                     view: "toolbar",
                     borderless: true,
                     cols: [
                         {
                             template: "<img src='../../images/logo.png' style='height:40px; width:40px'>",
                             height: 45, width: 65, borderless: true,
                         },
                         {},
                         {
                             cols: [
                                 { view: "label", label: "¿Nuevo en NutriSaaS?", width: 180},
                                 { view: "button", id: "registrarBtn", value: "Registrarse", css: "webix_primary", width: 100 }
                             ],
                             align: "middle"
                         }
                     ]
                 },
                 {
                     rows: [
                         {height: 50},
                         {
                             cols: [
                                 {}, // espacio izquierdo flexible
                                 {
                                     view: "form",
                                     id: "loginForm",
                                     width: 400,
                                     padding: 0,
                                     elements: [
                                         {cols: [
                                                 { view: "label", label: "Inicia sesión en NutriSaaS", css: "custom_form_title_label"}]
                                         },
                                         {cols: [
                                            { width: 10 },
                                            { view: "text", label: "", name: "email", placeholder: "Correo electrónico", invalidMessage: "Debe ser un correo válido" , height: 45 },
                                            { width: 10 },
                                         ]},
                                         {cols: [
                                            { width: 10 },
                                            { view: "text", type: "password", label: "", name: "password", placeholder: "Contraseña", invalidMessage: "Debe tener al menos 6 caracteres" , height: 45  },
                                            { width: 10 },
                                         ]},
                                         {cols: [
                                            { width: 8 },
                                            { view: "checkbox", labelWidth: 0, labelRight: "Recordarme", name: "rememberMe" }
                                         ]},
                                         {cols: [
                                            { width: 8 },
                                            { view: "button", id: "loginBtn", value: "Acceder a mi cuenta", css: "webix_primary" , height: 45},
                                            { width: 8 },
                                         ]},
                                         {height: 5}
                                     ],
                                     rules:{
                                          "email": webix.rules.isEmail,
                                          "password": function(value){ return value.length >= 4; }
                                     },
                                     elementsConfig:{
                                          invalidMessage: "Campo inválido"
                                     }
                                 },
                                 {} // espacio derecho flexible
                             ]
                         }
                     ]
                 }
             ]
         };
}
