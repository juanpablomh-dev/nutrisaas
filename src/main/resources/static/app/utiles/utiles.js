
//webix.env.cdn = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2)) + "../externos/webix";
webix.env.cdn = "/externos/webix";
webix.i18n.setLocale("es-ES");

function Utiles() {}

Utiles.securePostLogin = function(url, payload, callback) {
    webix.ajax()
        .headers(header_base)
        .post(url, JSON.stringify(payload))
        .then(function(res) {
            // HTTP 2xx → normalizamos la respuesta
            let data;
            try {
                data = res.json();
            } catch(e) {
                data = {};
            }

            const response = {
                timestamp: new Date().toISOString(),
                message: data.message || "Login exitoso",
                error: data.error || "",
                status: res.status || http.K_CODIGO_OK,
                data: data.token || ""
            };
            if(callback) callback(response);
        })
        .fail(function(res) {
            // HTTP 4xx / 5xx → devolvemos tal cual la respuesta del backend
            let data;
            try {
                data = JSON.parse(res.response);
            } catch(e) {
                data = res.response;
            }
            if(callback) callback(data);
        });
};

Utiles.secureRequest = function(method, url, payload, callback) {
    const token = Utiles.getToken();
    if (!token) {
        webix.alert({
            title: "Error",
            text: "No hay sesión activa!",
            type:"alert-error"
        });

        window.location.href = "/app/" + ctes.K_CAMPO_URL_LOGIN;
        return;
    }

    const req = webix.ajax().headers({
        ...header_base,
        "Authorization": `Bearer ${token}`
    });

    let request;
    switch(method.toUpperCase()) {
        case "GET":
            request = req.get(url);
            break;
        case "POST":
            request = req.post(url, JSON.stringify(payload));
            break;
        case "PUT":
            request = req.put(url, JSON.stringify(payload));
            break;
        case "DELETE":
            request = req.del(url);
            break;
        default:
            throw new Error("Método HTTP no soportado: " + method);
    }

    request.then(res => {
        let data;
        try { data = res.json(); } catch(e) { data = {}; }

        const response = {
            timestamp: new Date().toISOString(),
            message: res.message || "Ok",
            error: res.error || "",
            status: res.status || http.K_CODIGO_OK,
            data: data || ""
        };
        if(callback) callback(response);
    }).fail(res => {
        let data;
        try { data = JSON.parse(res.response); } catch(e) { data = { message: res.response }; }

        // Manejo de errores de autenticación
        if (res.status === 401 || res.status === 403) {
            localStorage.removeItem(ctes.K_CAMPO_TOKEN);
            window.location.href = "/app/" + ctes.K_CAMPO_URL_LOGIN;
            return;
        }

        if(callback) callback(data);
    });
};

Utiles.guardarToken = function(token, rememberMe) {
    localStorage.setItem(ctes.K_CAMPO_TOKEN, token);
};

Utiles.eliminarToken = function() {
    localStorage.removeItem(ctes.K_CAMPO_TOKEN);
};

Utiles.getToken = function() {
    return localStorage.getItem(ctes.K_CAMPO_TOKEN) ;
};

Utiles.calculateAge = function(birthDate) {
    const today = new Date();
    const birth = new Date(birthDate);

    let age = today.getFullYear() - birth.getFullYear();
    const m = today.getMonth() - birth.getMonth();

    if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) {
        age--;
    }
    return age;
};

Utiles.parseDate = function(value) {
    if (!value) return null;

    if (value instanceof Date) return new Date(value);

    let date = null;

    // Formato con hora: yyyy-mm-dd HH:ii:ss
    if (/^\d{4}-\d{2}-\d{2}\s+\d{2}:\d{2}:\d{2}$/.test(value)) {
        date = webix.Date.strToDate("%Y-%m-%d %H:%i:%s")(value);
    }

    // Solo fecha ISO: yyyy-mm-dd
    else if (/^\d{4}-\d{2}-\d{2}$/.test(value)) {
        date = webix.Date.strToDate("%Y-%m-%d")(value);
    }

    // Formato local: dd/mm/yyyy
    else if (/^\d{1,2}\/\d{1,2}\/\d{4}$/.test(value)) {
        date = webix.Date.strToDate("%d/%m/%Y")(value);
    }

    if (!date || isNaN(date.getTime())) return null;

    // Normalizar horas
    date.setHours(0, 0, 0, 0);
    return date;
};

// Convierte "dd/MM/yyyy HH:mm" o "dd/MM/yyyy" a Date.
// Si el string no coincide devuelve null.
Utiles.parseDatepicker = function(str) {
    if (!str || typeof str !== "string") return null;
    // separar fecha y hora
    const parts = str.trim().split(" ");
    const datePart = parts[0];
    const timePart = parts[1] || "00:00";

    const dateParts = datePart.split("/").map(s => parseInt(s, 10));
    if (dateParts.length !== 3) return null;
    let [day, month, year] = dateParts;
    if (isNaN(day) || isNaN(month) || isNaN(year)) return null;

    // Si el año viene en 2 dígitos (p. ej. "25"), convertir a 4 digitos (2000+)
    if (year < 100) year = 2000 + year;

    const timeParts = timePart.split(":").map(s => parseInt(s, 10));
    let hours = timeParts[0] || 0;
    let minutes = timeParts[1] || 0;
    if (isNaN(hours)) hours = 0;
    if (isNaN(minutes)) minutes = 0;

    // crear Date en zona local
    return new Date(year, month - 1, day, hours, minutes, 0, 0);
};

Utiles.formatDateForServer = function(date) {
    const d = Utiles.parseDate(date);
    if (!d) return null;
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
};

Utiles.fixDateTimeForBackend = function(value) {
    if (!value || typeof value !== "string") return value;
    // Si ya tiene la "T", no hace nada
    if (value.includes("T")) return value;
    // Si tiene espacio entre fecha y hora, lo reemplaza
    return value.replace(" ", "T");
};

// Helper para formatear Date a "dd/MM/yyyy HH:mm"
Utiles.formatForDatepicker = function(dateObj) {
    if (!dateObj || !(dateObj instanceof Date)) return "";
    const pad = (n) => (n < 10 ? "0" + n : String(n));
    const d = pad(dateObj.getDate());
    const m = pad(dateObj.getMonth() + 1);
    const yyyy = dateObj.getFullYear();
    const hh = pad(dateObj.getHours());
    const mm = pad(dateObj.getMinutes());
    return `${d}/${m}/${yyyy} ${hh}:${mm}`;
};

Utiles.formatDateFriendly = function(iso) {
    if (!iso) return "";
    // Asegurar formato compatible
    const d = new Date(iso);
    if (isNaN(d.getTime())) return iso; // fallback si no parsea

    const pad = n => n.toString().padStart(2, "0");
    const dd = pad(d.getDate());
    const mm = pad(d.getMonth() + 1);
    const yyyy = d.getFullYear();
    const hh = pad(d.getHours());
    const min = pad(d.getMinutes());

    return `${dd}/${mm}/${yyyy} ${hh}:${min}`;
}

Utiles.normalizeId = function(id) {
    // retorna 0 si el valor no es un número
    // si es un número, retorna el mismo número
    const num = Number(id);
    if (!num || isNaN(num)) return 0;
    return num;
};

Utiles.generateUUID = function() {
    // Crea un UUID v4 (aleatorio)
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            const r = Math.random() * 16 | 0;
            const v = c === 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        }
    );
};
