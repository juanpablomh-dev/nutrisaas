// dashboard.evolution.logic.js
// Lógica del módulo "Evolución de Métricas del Paciente" - versión comparador 2-columnas

class DashboardEvolutionLogic {

    static instances = {};

    constructor(patientId) {
        this.patientId = patientId;
        this.metricsMetadata = {};
        this.history = [];
        this.currentMetricA = null;
        this.currentMetricB = null;
        this.currentPaceMetric = null;
    }

    static toLocalDateTimeIso(dt) {
        const pad = (n) => n.toString().padStart(2, "0");
        return `${dt.getFullYear()}-${pad(dt.getMonth()+1)}-${pad(dt.getDate())}T${pad(dt.getHours())}:${pad(dt.getMinutes())}:${pad(dt.getSeconds())}`;
    }

    getDefaultRange() {
        const to = new Date();
        const from = new Date();
        from.setFullYear(to.getFullYear() - 5);
        from.setHours(0,0,0,0);
        to.setHours(23,59,59,999);
        return { from, to };
    }

    async loadAll() {
        await this.loadMetadata();
        await this.loadHistory();
    }

    loadMetadata() {
        return new Promise((resolve, reject) => {
            const url = services.K_SERVICE_DASHBOARD_GET + "/" + this.patientId + "/metrics";

            Utiles.secureRequest("GET", url, "", (response) => {
                const payload = Array.isArray(response) ? response : response?.data;
                if (!payload) return reject();

                this.metricsMetadata = Array.isArray(payload)
                    ? payload.reduce((a,p)=>{a[p.symbol||p.id]=p.name||p.value;return a;},{})
                    : payload;

                const options = Object.keys(this.metricsMetadata)
                    .map(k => ({ id:k, value:this.metricsMetadata[k] }));

                ["A","B"].forEach(s => {
                    const sel = $$(`evoMetricSelector_${this.patientId}_${s}`);
                    if (sel) {
                        sel.define("options", options);
                        sel.refresh();
                        if (!sel.getValue() && options[0]) sel.setValue(options[s==="A"?0:1] ? options[1].id : options[0].id);
                    }
                });

                const paceSel = $$(`evoPaceMetric_${this.patientId}`);
                if (paceSel && !paceSel._bound) {
                    paceSel._bound = true;
                    paceSel.define("options", options);
                    paceSel.refresh();
                    paceSel.attachEvent("onChange", m => this.updatePace(m));
                }

                resolve();
            });
        });
    }

    loadHistory() {
        return new Promise((resolve, reject) => {
            const { from, to } = this.getDefaultRange();
            const url =
                services.K_SERVICE_DASHBOARD_GET + "/" + this.patientId +
                "/history?from=" + DashboardEvolutionLogic.toLocalDateTimeIso(from) +
                "&to=" + DashboardEvolutionLogic.toLocalDateTimeIso(to);

            Utiles.secureRequest("GET", url, "", (response) => {
                const arr = Array.isArray(response) ? response : response?.data;
                if (!Array.isArray(arr)) return reject();

                this.history = arr.map(item => {
                    const raw = item.date || item.startTime;
                    return {
                        date: Utiles.formatDateFriendly(raw),
                        dateObj: raw ? new Date(raw) : null,
                        measurements: item.measurements || {},
                        calculated: item.calculated || {}
                    };
                });

                if (this.currentMetricA) this.updateMetric(this.currentMetricA,"A");
                if (this.currentMetricB) this.updateMetric(this.currentMetricB,"B");

                resolve();
            });
        });
    }

    updateMetric(metric, side) {
        if (!metric) return;
        if (side==="A") this.currentMetricA = metric;
        else this.currentMetricB = metric;

        const data = this.history
            .map(h=>{
                const s = h.measurements[metric] || h.calculated[metric];
                return s ? { date:h.date, value:s.value, unit:s.unit||"" } : null;
            })
            .filter(Boolean);

        $$(`evoChart_${this.patientId}_${side}`)?.clearAll();
        $$(`evoChart_${this.patientId}_${side}`)?.parse(data);

        $$(`evoTable_${this.patientId}_${side}`)?.clearAll();
        $$(`evoTable_${this.patientId}_${side}`)?.parse(data);
    }

    updatePace(metric) {
        const tableId = `evoPaceTable_${this.patientId}`;
        const summaryId = `evoPaceSummary_${this.patientId}`;

        $$(tableId)?.clearAll();
        $$(summaryId)?.setHTML("Datos insuficientes");

        if (!metric || this.history.length < 2) {
            return;
        }

        // SERIE CORRECTA
        const series = this.history
            .map(h => {
                const s = h.measurements[metric] || h.calculated[metric];
                if (!s || s.value == null || !h.dateObj) return null;
                return {
                    dateObj: h.dateObj,
                    dateStr: h.date,
                    value: s.value,
                    unit: s.unit || ""
                };
            })
            .filter(Boolean)
            .sort((a,b)=>a.dateObj - b.dateObj);

        if (series.length < 2) return;

        const MS_PER_DAY = 86400000;
        const rows = [];
        let sum = 0, count = 0;

        for (let i=1;i<series.length;i++) {
            const prev = series[i-1];
            const curr = series[i];

            const days = Math.max(1, Math.round((curr.dateObj - prev.dateObj) / MS_PER_DAY));
            const delta = curr.value - prev.value;
            const paceRaw = delta / days;
            const pace = paceRaw != null ? Number(paceRaw.toFixed(3)) : null;

            rows.push({
                fromDate: prev.dateStr,
                toDate: curr.dateStr,
                deltaValue: delta,
                deltaDays: days,
                pace: pace,
                unit: `${curr.unit}/día`
            });

            sum += pace;
            count++;
        }

        $$(tableId)?.clearAll();
        $$(tableId)?.parse(rows);

        const avg = sum / count;

        $$(summaryId)?.setHTML(`
            <div style="padding:6px;">
                <b>${this.metricsMetadata[metric]}</b>  -  Velocidad promedio: <b>${avg.toFixed(3)}</b> ${rows[0].unit}
            </div>
        `);
    }

    static getInstance(id) {
        return this.instances[id] ||= new DashboardEvolutionLogic(id);
    }
}
