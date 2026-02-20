class Dashboard {
    constructor() {
        this.title = "Dashboard inicializado";
    }

    static init() {
        console.log(this.title);
        // Aquí iría cualquier inicialización de UI o lógica específica
    }

    static registerInitializer() {
        UtilesForm.registerInitializer(menu.K_MENU_DASHBOARD, (cellId) => {
            Dashboard.init();
        });
    }
}


Dashboard.registerInitializer();
