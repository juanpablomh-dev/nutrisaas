
webix_skin = "material";

let ctes = {
    K_CAMPO_URL_LOGIN: "login.html",
    K_CAMPO_URL_APP: "/app/app.html",
    K_CAMPO_FORM_LOGIN: "loginForm",
    K_CAMPO_BOTON_LOGIN: "loginBtn",
    K_CAMPO_BOTON_LOGOUT: "logoutBtn",
    K_CAMPO_TOKEN: "authToken",
};

let menu = {
    K_ELEMENTO_SIDEBAR: "sidebar",
    K_ELEMENTO_CONTENT: "content",
    K_MENU_DASHBOARD: "dashboard",
    K_MENU_UNITS: "units",
    K_MENU_MEASUREMENT_TYPES: "measurement_types",
    K_MENU_PATIENTS: "patients",
    K_MENU_APPOINTMENTS: "appointments",
    K_MENU_MEASUREMENT_REMOTE: "measurement_remote"
};

let evento = {
    K_EVENTO_ITEM_CLICK: "onItemClick",
    K_EVENTO_AFTER_SELECT: "onAfterSelect"
};

let header_base = {
    "Content-Type":"application/json"
};

let http = {
    K_CODIGO_OK: 200,
    K_CODIGO_UNAUTHORIZED: 401,
    K_CODIGO_FORBIDDEN: 403
};

let services = {
    // Login
    K_SERVICE_LOGIN: "/auth/login",
    // Units
    K_SERVICE_UNIT_GET: "/api/tenant/units",
    K_SERVICE_UNIT_POST: "/api/tenant/units",
    K_SERVICE_UNIT_PUT: "/api/tenant/units",
    K_SERVICE_UNIT_DELETE: "/api/tenant/units",
    // Measurement Type
    K_SERVICE_MEASUREMENT_TYPE_GET: "/api/tenant/measurement-types",
    K_SERVICE_MEASUREMENT_TYPE_POST: "/api/tenant/measurement-types",
    K_SERVICE_MEASUREMENT_TYPE_PUT: "/api/tenant/measurement-types",
    K_SERVICE_MEASUREMENT_TYPE_DELETE: "/api/tenant/measurement-types",
    // Patient
    K_SERVICE_PATIENT_GET: "/api/tenant/patients",
    K_SERVICE_PATIENT_POST: "/api/tenant/patients",
    K_SERVICE_PATIENT_PUT: "/api/tenant/patients",
    K_SERVICE_PATIENT_DELETE: "/api/tenant/patients",
    // Appointment
    K_SERVICE_APPOINTMENT_GET: "/api/tenant/appointments",
    K_SERVICE_APPOINTMENT_POST: "/api/tenant/appointments",
    K_SERVICE_APPOINTMENT_PUT: "/api/tenant/appointments",
    K_SERVICE_APPOINTMENT_DELETE: "/api/tenant/appointments",
    K_SERVICE_APPOINTMENT_POST_LIST: "/api/tenant/appointments/list",
    // Dashboard
    K_SERVICE_DASHBOARD_GET: "/api/tenant/dashboard/patient"
};