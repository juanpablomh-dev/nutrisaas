function LoginLogic() {
    const form = $$(ctes.K_CAMPO_FORM_LOGIN);

    $$(ctes.K_CAMPO_BOTON_LOGIN).attachEvent(evento.K_EVENTO_ITEM_CLICK, function() {
        if(form.validate()) {
            const values = form.getValues();
            values.rememberMe = !!values.rememberMe;

			Utiles.securePostLogin(services.K_SERVICE_LOGIN, values, function(res) {
				if(res.status === 200 && res.data) {
					Utiles.guardarToken(res.data, values.rememberMe);
					webix.alert("Bienvenido " + values.email);
					window.location.href = ctes.K_CAMPO_URL_APP;
				} else {
					webix.alert({
                        title: "Error",
                        text: res.message,
                        type:"alert-error"
                    });
				}
			});
        } else {
            webix.alert({
                title: "Error",
                text: "Complete email y contraseña",
                type:"alert-error"
            });
        }
    });
}
