function AppLogic() {
    const sidebar = $$(menu.K_ELEMENTO_SIDEBAR);
    const content = $$(menu.K_ELEMENTO_CONTENT);

    // Arrancar en Dashboard
    UtilesForm.setupView(sidebar, content, menu.K_MENU_DASHBOARD);

    // Navegación
    UtilesForm.setupSidebarNavigation(sidebar, content);

    // Logout
    $$(ctes.K_CAMPO_BOTON_LOGOUT).attachEvent(evento.K_EVENTO_ITEM_CLICK, function(){
        Utiles.eliminarToken(ctes.K_CAMPO_TOKEN);
        window.location.href = "/app/" + ctes.K_CAMPO_URL_LOGIN;
    });
}
