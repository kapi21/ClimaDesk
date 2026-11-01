# Sunset Dim Overlay Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Atenuar gradualmente la UI con un overlay negro según amanecer/atardecer de Open-Meteo, y un botón superior de test que simula el ciclo completo en ~12 s.

**Architecture:** Overlay `#dim-overlay` (`pointer-events: none`, opacidad 0–0.30) actualizado cada segundo. Función pura `dimOpacity` + demo acelerada. Misma lógica en las tres variantes de dispositivo y en `main`; sin tocar brillo Android ni `MainActivity.java`.

**Tech Stack:** HTML/CSS/JS en WebView assets, Open-Meteo `daily.sunrise`/`sunset`, Node para asserts de la curva, Gradle/`gradlew.bat` en Windows PowerShell.

## Global Constraints

- Spec: `docs/superpowers/specs/2026-11-01-sunset-dim-overlay-design.md`
- Mecanismo: overlay negro (no `screenBrightness`)
- Rampa: 30 min; noche: opacidad overlay `0.30` (~70 % visible)
- Demo: ~12 s, cancelable con segundo toque; botón sin `disabled`
- Overlay z-index **por debajo** de city-picker / settings-modal
- Tema día/noche CSS existente: sin unificar en este plan
- Commits / APKs / installs: **solo si el usuario autoriza**
- Rutas con espacios: siempre entre comillas (`"C:\@MIS PROYECTOS\ClimaDesk"`)

## File map

| Archivo | Rama | Acción |
|---------|------|--------|
| `kiosk-app/app/src/main/assets/index.html` | `redmi4x-lite` | Overlay + botón + JS (ya tiene sunrise/sunset) |
| `kiosk-app/app/src/main/assets/index.html` | `tablet-galaxy-tab2` | Portar mismos cambios |
| `kiosk-app/app/src/main/assets/index.html` | `tablet-galaxy-tab2-color` | Portar mismos cambios |
| `kiosk-app/app/src/main/assets/index.html` | `main` | Añadir sunrise/sunset a daily + mismos cambios |
| `kiosk-app/app/build.gradle.kts` | cada rama de código | Bump de versión al entregar |
| `HANDOFF.md` / `ROADMAP.md` | `main` | Documentar feature |
| Spec | `main` | Ya escrito; commit si el usuario lo pide |

**Versiones sugeridas al cerrar cada rama:** Lite `1.8.2-lite` / `12`; Tab mono y color `1.8.1` / `10`; `main` `1.8.1` / `10`.

---

### Task 1: Función pura `dimOpacity` (TDD en Node)

**Files:**
- No crear archivo permanente: validar con `node -e` en PowerShell; la función se copiará literal a cada `index.html` en tasks siguientes.

**Interfaces:**
- Consumes: epoch ms (`nowMs`, `sunriseTodayMs`, `sunsetTodayMs`, `sunriseTomorrowMs`)
- Produces: `dimOpacity(nowMs, sunriseTodayMs, sunsetTodayMs, sunriseTomorrowMs) → number` en `[0, 0.30]`

- [ ] **Step 1: Escribir asserts que fallen (función aún no definida en el one-liner)**

En PowerShell, desde cualquier cwd:

```powershell
node -e "function dimOpacity(){ throw new Error('not implemented'); } try { dimOpacity(0,1,2,3); process.exit(1);} catch(e){ if(e.message==='not implemented') { console.log('fail-as-expected'); process.exit(0);} throw e;}"
```

Expected: `fail-as-expected` exit 0

- [ ] **Step 2: Implementar la función y pasar la batería de casos**

```powershell
node -e @"
const RAMP = 30 * 60 * 1000;
const MAX = 0.30;
function dimOpacity(nowMs, sunriseTodayMs, sunsetTodayMs, sunriseTomorrowMs) {
  if (!Number.isFinite(sunriseTodayMs) || !Number.isFinite(sunsetTodayMs)) return 0;
  const nextSr = Number.isFinite(sunriseTomorrowMs) ? sunriseTomorrowMs : (sunriseTodayMs + 24 * 3600 * 1000);
  if (nowMs < sunriseTodayMs) return MAX;
  if (nowMs < sunriseTodayMs + RAMP) return MAX * (1 - (nowMs - sunriseTodayMs) / RAMP);
  if (nowMs < sunsetTodayMs) return 0;
  if (nowMs < sunsetTodayMs + RAMP) return MAX * ((nowMs - sunsetTodayMs) / RAMP);
  if (nowMs < nextSr) return MAX;
  if (nowMs < nextSr + RAMP) return MAX * (1 - (nowMs - nextSr) / RAMP);
  return 0;
}
const sr = Date.parse('2026-11-01T07:00:00');
const ss = Date.parse('2026-11-01T18:00:00');
const sr2 = Date.parse('2026-11-02T07:00:00');
function approx(a,b){ return Math.abs(a-b) < 1e-9; }
const cases = [
  [Date.parse('2026-11-01T03:00:00'), MAX],
  [sr, MAX],
  [sr + 15*60*1000, 0.15],
  [sr + RAMP, 0],
  [Date.parse('2026-11-01T12:00:00'), 0],
  [ss, 0],
  [ss + 15*60*1000, 0.15],
  [ss + RAMP, MAX],
  [Date.parse('2026-11-02T03:00:00'), MAX],
  [sr2, MAX],
  [sr2 + RAMP, 0],
];
for (const [t, want] of cases) {
  const got = dimOpacity(t, sr, ss, sr2);
  if (!approx(got, want)) {
    console.error('FAIL', new Date(t).toISOString(), 'got', got, 'want', want);
    process.exit(1);
  }
}
if (dimOpacity(0, NaN, ss, sr2) !== 0) { console.error('FAIL missing sunrise'); process.exit(1); }
console.log('dimOpacity ok');
"@
```

Expected: `dimOpacity ok`

- [ ] **Step 3: Commit (solo si el usuario autoriza)** — este task no toca el repo; omitir commit o incluir solo el plan/spec en `main` si aún no están.

---

### Task 2: Feature completa en `redmi4x-lite`

**Files:**
- Modify: `kiosk-app/app/src/main/assets/index.html`
- Modify: `kiosk-app/app/build.gradle.kts` (`versionName` `1.8.2-lite`, `versionCode` `12`)

**Interfaces:**
- Consumes: `todaySunriseStr` / `todaySunsetStr` (ISO) ya rellenados desde `d.sunrise[1]` / `d.sunset[1]`; API con `past_days=1` + `forecast_days=2` (índices: 0 ayer, 1 hoy, 2 mañana)
- Produces: overlay vivo + `#btn-dim-test` + `sunTimes` con tomorrow sunrise

- [ ] **Step 1: Checkout**

```powershell
cd "C:\@MIS PROYECTOS\ClimaDesk"
git status
git checkout redmi4x-lite
```

Expected: `On branch redmi4x-lite`

- [ ] **Step 2: CSS del overlay**

Añadir en `<style>` (junto a otros fixed overlays):

```css
#dim-overlay {
  position: fixed;
  inset: 0;
  background: #000;
  opacity: 0;
  pointer-events: none;
  z-index: 90; /* city-picker 100, settings-modal 120 */
  transition: opacity 0.9s linear;
}
#btn-dim-test.busy {
  opacity: 0.55;
}
```

- [ ] **Step 3: Markup**

Justo después de `<body>` (o antes de cerrar `</body>`, pero fijo):

```html
<div id="dim-overlay" aria-hidden="true"></div>
```

En `.top-right`, **antes** de `#btn-auto-locate` (o justo después de `#btn-theme-toggle`):

```html
<span class="status-icon" id="btn-dim-test" title="Probar atenuacion" role="button" aria-label="Probar atenuacion">◐</span>
```

- [ ] **Step 4: JS — constantes, estado, `dimOpacity`, resolución solar, apply, demo**

Tras las variables `todaySunriseStr` / `todaySunsetStr`, añadir:

```javascript
var RAMP_MS = 30 * 60 * 1000;
var DIM_MAX = 0.30;
var DIM_DEMO_MS = 12000;
var dimDemoActive = false;
var dimDemoTimer = null;
var dimDemoRaf = null;
var tomorrowSunriseStr = null;

function dimOpacity(nowMs, sunriseTodayMs, sunsetTodayMs, sunriseTomorrowMs) {
  if (!Number.isFinite(sunriseTodayMs) || !Number.isFinite(sunsetTodayMs)) return 0;
  var nextSr = Number.isFinite(sunriseTomorrowMs) ? sunriseTomorrowMs : (sunriseTodayMs + 24 * 3600 * 1000);
  if (nowMs < sunriseTodayMs) return DIM_MAX;
  if (nowMs < sunriseTodayMs + RAMP_MS) return DIM_MAX * (1 - (nowMs - sunriseTodayMs) / RAMP_MS);
  if (nowMs < sunsetTodayMs) return 0;
  if (nowMs < sunsetTodayMs + RAMP_MS) return DIM_MAX * ((nowMs - sunsetTodayMs) / RAMP_MS);
  if (nowMs < nextSr) return DIM_MAX;
  if (nowMs < nextSr + RAMP_MS) return DIM_MAX * (1 - (nowMs - nextSr) / RAMP_MS);
  return 0;
}

function parseSunIso(iso) {
  if (!iso) return NaN;
  var t = Date.parse(iso);
  return Number.isFinite(t) ? t : NaN;
}

function applyDimOverlay(now) {
  if (dimDemoActive) return;
  var el = document.getElementById('dim-overlay');
  if (!el) return;
  var sr = parseSunIso(todaySunriseStr);
  var ss = parseSunIso(todaySunsetStr);
  var sr2 = parseSunIso(tomorrowSunriseStr);
  el.style.opacity = String(dimOpacity(now.getTime(), sr, ss, sr2));
}

function demoOpacity(progress01) {
  var p = Math.max(0, Math.min(1, progress01));
  // 0–0.12 day | 0.12–0.38 evening ramp | 0.38–0.62 night | 0.62–0.88 morning ramp | 0.88–1 day
  if (p < 0.12) return 0;
  if (p < 0.38) return DIM_MAX * ((p - 0.12) / 0.26);
  if (p < 0.62) return DIM_MAX;
  if (p < 0.88) return DIM_MAX * (1 - (p - 0.62) / 0.26);
  return 0;
}

function stopDimDemo() {
  dimDemoActive = false;
  if (dimDemoTimer) { clearTimeout(dimDemoTimer); dimDemoTimer = null; }
  if (dimDemoRaf) { cancelAnimationFrame(dimDemoRaf); dimDemoRaf = null; }
  var btn = document.getElementById('btn-dim-test');
  if (btn) btn.classList.remove('busy');
  applyDimOverlay(new Date());
}

function startDimDemo() {
  if (dimDemoActive) { stopDimDemo(); return; }
  dimDemoActive = true;
  var btn = document.getElementById('btn-dim-test');
  if (btn) btn.classList.add('busy');
  var el = document.getElementById('dim-overlay');
  var t0 = Date.now();
  function frame() {
    if (!dimDemoActive) return;
    var p = (Date.now() - t0) / DIM_DEMO_MS;
    if (el) el.style.opacity = String(demoOpacity(p));
    if (p >= 1) { stopDimDemo(); return; }
    dimDemoRaf = requestAnimationFrame(frame);
  }
  dimDemoRaf = requestAnimationFrame(frame);
  dimDemoTimer = setTimeout(function () { stopDimDemo(); }, DIM_DEMO_MS + 50);
}
```

- [ ] **Step 5: Enganchar datos solares de “mañana”**

Donde se asignan `todaySunriseStr` / `todaySunsetStr` desde `d.sunrise[1]` / `d.sunset[1]`, añadir:

```javascript
if (d.sunrise && d.sunrise[2]) {
  tomorrowSunriseStr = d.sunrise[2];
} else {
  tomorrowSunriseStr = null;
}
```

- [ ] **Step 6: Llamar `applyDimOverlay` desde `updateClock`**

Al final de `updateClock()`:

```javascript
applyDimOverlay(now);
```

- [ ] **Step 7: Listener del botón en `init`**

```javascript
var btnDim = document.getElementById('btn-dim-test');
if (btnDim) {
  btnDim.addEventListener('click', startDimDemo);
}
```

- [ ] **Step 8: Bump de versión**

En `kiosk-app/app/build.gradle.kts`:

```kotlin
versionCode = 12
versionName = "1.8.2-lite"
```

- [ ] **Step 9: Verificar en Node la misma `dimOpacity` pegada (copiar cuerpo) + smoke visual**

Re-ejecutar el `node -e` del Task 1 Step 2.  
Abrir el HTML en navegador o WebView: pulsar `◐` → ciclo ~12 s → vuelve.  
Si hay dispositivo: `gradlew.bat assembleDebug` e instalar **solo si el usuario lo pide**.

- [ ] **Step 10: Commit (solo si el usuario autoriza)**

```powershell
git add kiosk-app/app/src/main/assets/index.html kiosk-app/app/build.gradle.kts
git commit -m "feat(lite): gradual dim overlay by sunrise/sunset with test button"
```

---

### Task 3: Portar a `tablet-galaxy-tab2`

**Files:**
- Modify: `kiosk-app/app/src/main/assets/index.html` (misma estructura que Lite: `status-icon`, `todaySunriseStr`, `updateClock`, `past_days=1`)
- Modify: `kiosk-app/app/build.gradle.kts` → `versionCode = 10`, `versionName = "1.8.1"`

**Interfaces:**
- Consumes: mismos IDs/patrones que Lite (`btn-auto-locate`, `updateClock`, `d.sunrise[1|2]`)
- Produces: misma API de runtime (`dimOpacity`, `startDimDemo`, `#dim-overlay`, `#btn-dim-test`)

- [ ] **Step 1: Checkout**

```powershell
cd "C:\@MIS PROYECTOS\ClimaDesk"
git checkout tablet-galaxy-tab2
```

- [ ] **Step 2: Aplicar los mismos bloques CSS / HTML / JS / bump del Task 2**

Comprobar z-index del city-picker/settings en esta rama; si difieren, dejar overlay en `90` o `max(modales)-10` (por debajo del menor modal).

- [ ] **Step 3: Verificar botón test ~12 s**

- [ ] **Step 4: Commit (solo si el usuario autoriza)**

```powershell
git add kiosk-app/app/src/main/assets/index.html kiosk-app/app/build.gradle.kts
git commit -m "feat(tab-mono): gradual dim overlay by sunrise/sunset with test button"
```

---

### Task 4: Portar a `tablet-galaxy-tab2-color`

**Files:**
- Modify: `kiosk-app/app/src/main/assets/index.html`
- Modify: `kiosk-app/app/build.gradle.kts` → `versionCode = 10`, `versionName = "1.8.1"`

**Interfaces:**
- Igual que Task 3

- [ ] **Step 1: Checkout `tablet-galaxy-tab2-color`**

- [ ] **Step 2: Mismos cambios CSS/HTML/JS/bump**

En tema claro E-Ink el velo negro se nota más: es intencional (spec).

- [ ] **Step 3: Verificar demo + que city-picker quede por encima del overlay**

- [ ] **Step 4: Commit (solo si el usuario autoriza)**

```powershell
git add kiosk-app/app/src/main/assets/index.html kiosk-app/app/build.gradle.kts
git commit -m "feat(tab-color): gradual dim overlay by sunrise/sunset with test button"
```

---

### Task 5: Portar a `main` (añadir sunrise/sunset a la API)

**Files:**
- Modify: `kiosk-app/app/src/main/assets/index.html`
- Modify: `kiosk-app/app/build.gradle.kts` → `versionCode = 10`, `versionName = "1.8.1"`

**Interfaces:**
- Consumes: `tick()`, `loadWeather` / `renderWeather` / `restoreLastWeather`, `.top-right` con botones `.refresh`
- Produces: mismas funciones; en `main` los botones son `<button class="refresh">` (no `status-icon`)

- [ ] **Step 1: Checkout `main`**

```powershell
git checkout main
```

- [ ] **Step 2: Extender la URL daily**

En `loadWeather`, cambiar:

```javascript
+ "&daily=temperature_2m_max,temperature_2m_min,precipitation_probability_max"
```

por:

```javascript
+ "&daily=temperature_2m_max,temperature_2m_min,precipitation_probability_max,sunrise,sunset"
+ "&forecast_days=2"
```

(`forecast_days=2` ya está en `main`; no duplicar el parámetro).

- [ ] **Step 3: Estado solar en `main`**

```javascript
let todaySunriseStr = null;
let todaySunsetStr = null;
let tomorrowSunriseStr = null;
```

En `renderWeather`, tras leer `data.daily`:

```javascript
const d = data.daily || {};
if (d.sunrise && d.sunrise[0]) todaySunriseStr = d.sunrise[0];
if (d.sunset && d.sunset[0]) todaySunsetStr = d.sunset[0];
if (d.sunrise && d.sunrise[1]) tomorrowSunriseStr = d.sunrise[1];
else tomorrowSunriseStr = null;
```

(Nota: en `main` **no** hay `past_days=1` → índice 0 = hoy, 1 = mañana.)

- [ ] **Step 4: CSS overlay**

```css
#dim-overlay {
  position: fixed;
  inset: 0;
  background: #000;
  opacity: 0;
  pointer-events: none;
  z-index: 40;
  transition: opacity 0.9s linear;
}
.city-picker { /* asegurar z-index > 40 si no lo tiene */ }
```

Si `.city-picker` no define `z-index`, añadir `z-index: 50` (o el valor que ya use el panel para quedar por encima).

- [ ] **Step 5: Markup**

```html
<div id="dim-overlay" aria-hidden="true"></div>
```

En `.top-right`, antes de `#locate`:

```html
<button type="button" class="refresh" id="btn-dim-test" title="Probar atenuacion" aria-label="Probar atenuacion">◐</button>
```

- [ ] **Step 6: Pegar JS `dimOpacity` / demo / `applyDimOverlay` (mismas firmas que Task 2)** e invocar `applyDimOverlay(now)` al final de `tick()`.

- [ ] **Step 7: Listener**

```javascript
document.getElementById("btn-dim-test").addEventListener("click", startDimDemo);
```

- [ ] **Step 8: Bump `1.8.1` / `10` + verificar demo**

- [ ] **Step 9: Commit (solo si el usuario autoriza)**

```powershell
git add kiosk-app/app/src/main/assets/index.html kiosk-app/app/build.gradle.kts
git commit -m "feat: gradual dim overlay by sunrise/sunset with test button"
```

---

### Task 6: Docs en `main`

**Files:**
- Modify: `HANDOFF.md`
- Modify: `ROADMAP.md` (marcar atenuación nocturna overlay como hecha / parcial si aún no hay APKs)
- Optionally: `README.md` (gesto del botón `◐` en “Gestos útiles”)
- Ensure: `docs/superpowers/specs/2026-11-01-sunset-dim-overlay-design.md` y este plan están en `main`

- [ ] **Step 1: Actualizar HANDOFF** — fecha, feature atenuación, ramas tocadas, cómo probar el botón.

- [ ] **Step 2: ROADMAP** — en P2 “Ajuste fino de brillo / Sensor de luz”, anotar: overlay por sunrise/sunset ✅; brillo sistema / sensor pendiente.

- [ ] **Step 3: README** — una línea en gestos: `◐` — probar atenuación día/noche.

- [ ] **Step 4: Commit docs (solo si el usuario autoriza)**

```powershell
git add HANDOFF.md ROADMAP.md README.md docs/superpowers/specs/2026-11-01-sunset-dim-overlay-design.md docs/superpowers/plans/2026-11-01-sunset-dim-overlay.md
git commit -m "docs: sunset dim overlay spec, plan, and handoff"
```

---

## Self-review (plan vs spec)

| Requisito spec | Task |
|----------------|------|
| Overlay negro 0–0.30 | 2–5 |
| Rampas 30 min / ciclo A | 1–2 (`dimOpacity`) |
| Open-Meteo sunrise/sunset | 2–4 (ya en API); 5 (añadir en main) |
| Botón test ciclo ~12 s cancelable | 2–5 (`startDimDemo` / segundo toque) |
| Bajo city-picker | z-index 90 vs 100/120 (Lite/Tab); 40 vs 50 (`main`) |
| Tres variantes + main | Tasks 2–5 |
| Sin brillo Android | Global + ningún cambio Java |
| Verificación demo / offline cache | Steps de verify; cache: strings solares ya van en weather payload existente al guardar `data` completo en Lite; en `main` el `saveLastWeather` guarda `weather` entero → sunrise/sunset persisten tras Step 5 |

Sin placeholders pendientes. Firmas alineadas: `dimOpacity(nowMs, sunriseTodayMs, sunsetTodayMs, sunriseTomorrowMs)`.
