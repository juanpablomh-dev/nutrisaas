(function(){
  const DEFAULT_FORMAT = {
    thousandSeparator: ",",
    decimalSeparator: ".",
    decimals: 2,
    allowNegative: false,
    useIntl: true,   // usa Intl.NumberFormat si está disponible
    locale: undefined // e.g. "es-ES"
  };

  function escapeForRegex(s){
    return s.replace(/[-\/\\^$*+?.()|[\]{}]/g, "\\$&");
  }

  // normaliza la cadena a forma "1234.56" (punto decimal)
  function unformatToNormalized(str, opts){
    if (str === null || str === undefined) return "";
    let s = String(str).trim();

    // eliminar separador de miles conocido (por si el usuario pegó ya formateado)
    if (opts.thousandSeparator) {
      s = s.replace(new RegExp(escapeForRegex(opts.thousandSeparator), "g"), "");
    }
    // aceptar tanto el decimal configurado como punto
    if (opts.decimalSeparator && opts.decimalSeparator !== ".") {
      s = s.replace(new RegExp(escapeForRegex(opts.decimalSeparator), "g"), ".");
    }
    // limpiar todo menos dígitos y punto y posible leading '-'
    const allowNeg = opts.allowNegative;
    let negative = "";
    if (allowNeg && s.startsWith("-")) { negative = "-"; s = s.slice(1); }
    s = s.replace(/[^0-9.]/g, "");
    // mantener solo primera ocurrencia de punto
    const idx = s.indexOf(".");
    if (idx !== -1) s = s.slice(0, idx + 1) + s.slice(idx + 1).replace(/\./g, "");
    return (negative ? "-" : "") + s;
  }

  function parseNumberFromString(str, opts){
    const norm = unformatToNormalized(str, opts);
    if (norm === "" || norm === "-") return NaN;
    const n = parseFloat(norm);
    return Number.isFinite(n) ? n : NaN;
  }

  function formatNumberForDisplay(n, opts){
    if (n === null || n === undefined || Number.isNaN(Number(n))) return "";
    const decimals = Number.isFinite(opts.decimals) ? opts.decimals : 0;
    if (opts.useIntl && typeof Intl !== "undefined") {
      try {
        const nf = opts.locale ? new Intl.NumberFormat(opts.locale, { minimumFractionDigits: decimals, maximumFractionDigits: decimals }) :
                                  new Intl.NumberFormat(undefined, { minimumFractionDigits: decimals, maximumFractionDigits: decimals });
        return nf.format(Number(n));
      } catch(e) { /* fallback below */ }
    }
    // fallback simple si Intl no disponible
    const fixed = Number(n).toFixed(decimals);
    let [intPart, decPart] = fixed.split(".");
    intPart = intPart.replace(/\B(?=(\d{3})+(?!\d))/g, opts.thousandSeparator || ",");
    return decPart ? intPart + (opts.decimalSeparator || ".") + decPart : intPart;
  }

  // Componente: local_textNumeric
  webix.protoUI({
    name: "local_textNumeric",

    defaults: {
      numberFormat: Object.assign({}, DEFAULT_FORMAT)
    },

    $init: function(config){
      // merge config.numberFormat
      this._numberFormat = Object.assign({}, DEFAULT_FORMAT, config.numberFormat || {});
      // si pasas mask en config, Webix aplicará la máscara automáticamente
      if (config.mask) {
        // webix supports mask property on text inputs
        this.define("mask", config.mask);
      }

      // attach light handlers
      const self = this;

      // onKeyPress: usar e.key para cubrir distintos teclados
      this._keyHandler = this.attachEvent("onKeyPress", (code, e) => {
        try {
          if (e && (e.ctrlKey || e.metaKey)) return true;
          if ([8,9,13,27,37,38,39,40,46].indexOf(code) !== -1) return true;

          const key = (e && e.key) ? String(e.key) : String.fromCharCode(code);
          if (/^[0-9]$/.test(key)) return true;

          const dec = this._numberFormat.decimalSeparator || ".";
          if (key === dec || key === ".") {
            const cur = this.getValue() || "";
            if (cur.indexOf(dec) !== -1 || cur.indexOf(".") !== -1) return false;
            return true;
          }

          if (this._numberFormat.allowNegative && key === "-") {
            const cur = this.getValue() || "";
            return cur.length === 0;
          }
          return false;
        } catch (err) { return true; }
      });

      // onTimedKeyPress: normalizar pegados (mantener decimalSeparator for editing)
      this._timed = this.attachEvent("onTimedKeyPress", () => {
        const val = this.getValue() || "";
        const normalized = unformatToNormalized(val, this._numberFormat); // dot decimal
        let display = normalized;
        if (this._numberFormat.decimalSeparator && this._numberFormat.decimalSeparator !== ".") {
          display = normalized.replace(".", this._numberFormat.decimalSeparator);
        }
        if (display !== val) this.setValue(display);
      });

      // onFocus: (por defecto mantenemos el formato; si prefieres otra cosa se puede cambiar)
      this._focus = this.attachEvent("onFocus", () => {
        // Mantener la representación formateada en foco (no quitar puntos)
        const val = this.getValue() || "";
        const n = parseNumberFromString(val, this._numberFormat);
        if (!Number.isNaN(n)) {
          // mostrar formateado (con Intl/toLocaleString)
          this.setValue(formatNumberForDisplay(n, this._numberFormat));
        }
        // seleccionar contenido para facilitar reemplazo
        try { const node = this.getInputNode && this.getInputNode(); if (node) node.select(); } catch(e){}
      });

      // onBlur: formatear (display)
      this._blur = this.attachEvent("onBlur", () => {
        const val = this.getValue() || "";
        const n = parseNumberFromString(val, this._numberFormat);
        if (Number.isNaN(n)) {
          this.setValue("");
        } else {
          this.setValue(formatNumberForDisplay(n, this._numberFormat));
        }
      });

      // cleanup on destroy
      const origDestroy = this.$destroy;
      this.$destroy = function(){
        try {
          if (self._keyHandler) self.detachEvent(self._keyHandler);
          if (self._timed) self.detachEvent(self._timed);
          if (self._focus) self.detachEvent(self._focus);
          if (self._blur) self.detachEvent(self._blur);
        } catch(e){}
        if (typeof origDestroy === "function") origDestroy.apply(this, arguments);
      };
    },

    // public API
    setNumberFormat: function(formatOptions){
      this._numberFormat = Object.assign({}, DEFAULT_FORMAT, formatOptions || {});
      // reformat current value
      const n = parseNumberFromString(this.getValue() || "", this._numberFormat);
      if (!Number.isNaN(n)) this.setValue(formatNumberForDisplay(n, this._numberFormat));
    },

    setNumericValue: function(number){
      if (number === null || number === undefined || Number.isNaN(Number(number))) {
        this.setValue("");
        return;
      }
      this.setValue(formatNumberForDisplay(number, this._numberFormat));
    },

    getNumericValue: function(){
      const v = this.getValue() || "";
      const n = parseNumberFromString(v, this._numberFormat);
      return Number.isFinite(n) ? n : NaN;
    }

  }, webix.ui.text);

})();


webix.protoUI({
    name: "local_datepicker",

    $init: function (config) {
        config.stringResult = true;
        config.valueFormat = "%d/%m/%Y";
        config.format = "%d/%m/%Y";
        config.editable = true;
        config.invalidMessage = config.invalidMessage || "Debe ingresar una fecha válida";
        config.height = config.height || 38;
        config.css = (config.css || "") + " webix_el_text webix_el_local_datepicker";
    }
}, webix.ui.datepicker);