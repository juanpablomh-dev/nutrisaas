class PopupSelect {
    constructor(endpoint, callback, config = {}) {
        this.endpoint = endpoint;
        this.callback = callback;
        this.config = config;
    }

    async init() {
        // destruye si ya existe
        if ($$(this.config.id || "popupSelect")) $$(this.config.id || "popupSelect").destructor();

        webix.ui(PopupSelectUI(this.config)).show();

        this.grid = $$(this.config.gridId || "popupSelectGrid");
        this.btnSelect = $$(this.config.btnSelectId || "btnSelect");

        this.attachEvents();
        await this.loadData();
    }

    attachEvents() {
        if (this.btnSelect) {
            this.btnSelect.attachEvent("onItemClick", () => this.onSelect());
        }

        if (this.grid) {
            this.grid.attachEvent("onItemDblClick", (id) => this.onSelect(id));
        }
    }

    async loadData() {
        try {
            this.grid.showOverlay("Cargando datos...");
            Utiles.secureRequest("GET", this.endpoint, "", (data) => {
                this.loadDataCallback(data.data);
            });
        } catch (e) {
            console.error("Error al cargar datos:", e);
            this.grid.showOverlay("Error al cargar datos");
        } finally {
            this.grid.hideOverlay();
        }
    }

    loadDataCallback(data) {
        this.grid.clearAll();
        this.grid.parse(data);
        this.grid.filterByAll();
    }

    onSelect(id) {
        const selected = id ? this.grid.getItem(id) : this.grid.getSelectedItem();
        if (selected && this.callback) {
            this.callback(selected);
        }
        $$(this.config.id || "popupSelect").close();
    }
}
