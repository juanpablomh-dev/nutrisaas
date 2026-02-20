class Dashboard0001Logic {

    constructor(patientId) {
        this.patientId = patientId;
    }

    async loadDashboardData() {
        const patient = await this.loadPatientData(this.patientId);
        const latest = await this.loadKPIsData(this.patientId, "latest");
        const previous = await this.loadKPIsData(this.patientId, "previous");
        const history =  await this.loadHistoryData(this.patientId);

        // Simular delay corto
        await new Promise(r => setTimeout(r, 200));

        this.fillHeader(patient);
        this.fillKPIs(latest, previous);
        this.fillCharts(history);
        this.fillComparisonTable(latest, history);
        this.fillRecommendations(calculated);
    }

    fillHeader(patient) {
        const info =
            `<div>Edad: 39</div>
             <div>Género: ${patient.gender}</div>`;
        $$( `patientMeta_${this.patientId}` ).setHTML(info);
    }

    loadPatientData(patientId) {
        return new Promise((resolve, reject) => {
            const url = services.K_SERVICE_PATIENT_GET + "/" + patientId;

            Utiles.secureRequest("GET", url, "", (response) => {
                if (response && response.data) {
                    resolve(response.data);
                } else {
                    reject("No se pudo obtener el paciente");
                }
            });
        });
    }

    loadKPIsData(patientId, type) {
        return new Promise((resolve, reject) => {
            const url = services.K_SERVICE_DASHBOARD_GET + "/" + patientId + "/" + type;

            Utiles.secureRequest("GET", url, "", (response) => {
                if (response && response.data) {
                    resolve(response.data);
                } else {
                    reject("No se pudo obtener los datos del paciente");
                }
            });
        });
    }

    loadHistoryData(patientId) {
        return new Promise((resolve, reject) => {
            const today = new Date();
            const fromDate = new Date();
            fromDate.setFullYear(today.getFullYear() - 5);

            // convertir a ISO string para el backend
            const isoFrom = fromDate.toISOString();
            const isoTo = today.toISOString();

            const url = `${services.K_SERVICE_DASHBOARD_GET}/${patientId}/history?from=${encodeURIComponent(isoFrom)}&to=${encodeURIComponent(isoTo)}`;

            Utiles.secureRequest("GET", url, "", (response) => {
                if (response && response.data) {
                    resolve(response.data);
                } else {
                    reject("No se pudo obtener los datos del paciente");
                }
            });
        });
    }

    fillKPIs(latest, prev, metadata = {}) {

        const data = [];

        // 1) procesar mediciones físicas
        for (const key in latest.measurements) {
            const item = latest.measurements[key];
            const name = metadata[key] || item.name || key;

            const latestValue = item.value;
            const latestWithUnit = item.unit ? `${item.value} ${item.unit}` : item.value;

            // previous?
            let prevValue = null;
            if (prev && prev.measurements && prev.measurements[key]) {
                prevValue = prev.measurements[key].value;
            }

            let change = "";
            let changeColor = "gray";

            if (prevValue != null) {
                const diff = latestValue - prevValue;
                change = diff.toFixed(2);

                if (diff > 0) changeColor = "green";
                else if (diff < 0) changeColor = "red";
                else changeColor = "gray";
            }

            data.push({
                name,
                value: latestWithUnit,
                change,
                changeColor
            });
        }

        // 2) procesar mediciones calculadas
        for (const key in latest.calculated) {
            const itemCalculate = latest.calculated[key];
            const name = metadata[key] || itemCalculate.name || key;
            const value = itemCalculate.value || 0;

            let prevValue = null;
            if (prev && prev.calculated && prev.calculated[key] != null) {
                prevValue = prev.calculated[key].value;
            }

            let change = "";
            let changeColor = "gray";

            if (prevValue != null) {
                const diff = value - prevValue;
                change = diff.toFixed(2);
                if (diff > 0) changeColor = "orange";
                if (diff < 0) changeColor = "blue";
            }

            data.push({
                name,
                value: value.toFixed(2),
                change,
                changeColor
            });
        }

        // render
        const id = `kpiCards_${this.patientId}`;
        $$(id).clearAll();
        $$(id).parse(data);
    }

    fillCharts(history) {
        const weightData = history.map(h => ({ x: h.date, y: h.weight }));
        const bmiData = history.map(h => ({ x: h.date, y: h.bmi }));
        const fatData = history.map(h => ({ x: h.date, y: h.fat }));
        const whrData = history.map(h => ({ x: h.date, y: h.whr }));

        $$(`chartWeight_${this.patientId}`).parse(weightData);
        $$(`chartBMI_${this.patientId}`).parse(bmiData);
        $$(`chartFat_${this.patientId}`).parse(fatData);
        $$(`chartWHR_${this.patientId}`).parse(whrData);
    }

    fillComparisonTable(latest, history) {

        if (!latest || !history || history.length < 1) return;

        // el previo es el snapshot inmediatamente anterior al último
        const prevSnapshot = history.length > 1 ? history[history.length - 2] : null;

        const table = [];

        // ---- 1) MEDICIONES REALES ----
        for (const key in latest.measurements) {

            const lm = latest.measurements[key]; // real latest
            const lp = prevSnapshot?.measurements?.[key] || null; // real previous

            const lastVal = lm.value;
            const prevVal = lp ? lp.value : null;

            let delta = "";
            let percent = "";

            if (prevVal !== null) {
                const diff = lastVal - prevVal;
                delta = diff.toFixed(2);
                percent = ((diff / prevVal) * 100).toFixed(1) + "%";
            }

            table.push({
                metric: lm.name,
                last: lm.unit ? `${lastVal} ${lm.unit}` : lastVal,
                prev: lp ? (lp.unit ? `${prevVal} ${lp.unit}` : prevVal) : "",
                delta,
                percent
            });
        }

        // ---- 2) MEDICIONES CALCULADAS ----
        for (const key in latest.calculated) {

            const lm = latest.calculated[key];
            const lp = prevSnapshot?.calculated?.[key] || null;

            const lastVal = lm.value;
            const prevVal = lp ? lp.value : null;

            let delta = "";
            let percent = "";

            if (prevVal !== null) {
                const diff = lastVal - prevVal;
                delta = diff.toFixed(2);
                percent = ((diff / prevVal) * 100).toFixed(1) + "%";
            }

            table.push({
                metric: lm.name,
                last: lm.unit ? `${lastVal} ${lm.unit}` : lastVal,
                prev: lp ? (lp.unit ? `${prevVal} ${lp.unit}` : prevVal) : "",
                delta,
                percent
            });
        }

        // ---- render ----
        $$(`compareTable_${this.patientId}`).clearAll();
        $$(`compareTable_${this.patientId}`).parse(table);
    }


    fillRecommendations(calculated) {
        let html = "";

        if (calculated.bmi > 25) {
            html += "<p>• IMC elevado: considerar plan de reducción calórica moderada.</p>";
        }
        if (calculated.whr > 0.90) {
            html += "<p>• Riesgo abdominal aumentado: priorizar ejercicios de core.</p>";
        }

        $$(`recommendations_${this.patientId}`).setHTML(`
            <div style='padding:10px;'>${html || "Sin recomendaciones por ahora."}</div>
        `);
    }
}
