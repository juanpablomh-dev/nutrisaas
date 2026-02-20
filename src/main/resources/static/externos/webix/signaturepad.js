/*
 -------------------------------------------------------------------------------
 o CLASE................: signaturepad.js
 o CREADA POR...........: Pablo Deandreis
 o FECHA DE CREACIÓN....: 13/10/2023
 o FUNCION..............: Integración con componente signature-pad.
 o A MEDIDA PARA........: .
 o COMENTARIOS..........: .
 -------------------------------------------------------------------------------
 */

/* global webix, Utiles, ctes */

webix.protoUI({
    name: "signaturepadeditor",
    defaults: {
        css: "backgroundTransparent"
    },
    $init: function (config) {
        this.stage = webix.promise.defer().then(webix.bind(function () {
            try {
                let idCanvas = "canvas." + this.config.id;
                let template = "<canvas id='" + idCanvas + "' style='background-color:rgb(245,245,245)'></canvas>";
                this.define("template", template);
                this.refresh();
                let canvas = document.getElementById(idCanvas);
                Utiles.excepcionSi((canvas === null), "No se pudo recuperar el canvas para el componente signaturepadeditor.");
                canvas.height = ((this.config.height === 0) ? 100 : this.config.height);
                canvas.width = ((this.config.width === 0) ? 100 : this.config.width);
                this.signaturepad = new SignaturePad(canvas);
                if (this.config.base64 !== ctes.K_VACIO) {
                    this.signaturepad.fromDataURL("data:image/png;base64," + this.config.base64);
                }
                return this.signaturepad;
            } catch (err) {
                Utiles.mostrarVentanaMensaje(err);
            }
        }, this));
        if (window.SignaturePad)
            this.stage.resolve();
        else
            webix.require(Utiles.getContextPath() + "/dlsdkweb/web/externos/webix/signaturepad/signature_pad.js", function () {
                this.stage.resolve();
            }, this);
    },
    clearSignature: function () {
        this.signaturepad.clear();
    },
    getSignatureBase64: function () {
        let dataURL = this.signaturepad.toDataURL();
        let parts = dataURL.split(";base64,");
        return parts[1];
    }
}, webix.ui.template);