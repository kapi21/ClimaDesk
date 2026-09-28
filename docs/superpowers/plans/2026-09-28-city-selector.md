# City Selector (P1.1) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Sustituir `window.prompt` al elegir ciudad por un panel a pantalla completa con sugerencias en vivo de Open-Meteo.

**Architecture:** Toda la lógica vive en el WebView (`index.html`). Se añaden helpers de geocoding multi-resultado, persistencia lat/lon, y un overlay fullscreen; el botón 📍 limpia la ciudad manual. Sin cambios Java ni empaquetado APK.

**Tech Stack:** HTML/CSS/JS en WebView Android; Open-Meteo Geocoding API; `localStorage`.

## Global Constraints

- Solo modificar `kiosk-app/app/src/main/assets/index.html` para la feature (más docs de handoff/roadmap al cerrar).
- No empaquetar APK salvo petición explícita del usuario o necesidad estricta.
- No usar `AbortController`; usar contador de secuencia para búsquedas concurrentes.
- Panel: buscar / elegir / cancelar únicamente (sin GPS dentro del panel).
- Etiqueta de resultado: `nombre, admin1, país` (omitir partes vacías).
- `#city` muestra el nombre corto (`name`), no el label completo.
- Debounce ≈ 300 ms; mínimo 2 caracteres; `count=8`; `language=es`.
- Commits: solo si el usuario los autoriza en la sesión (regla del repo); si no, dejar cambios staged/unstaged y continuar.

## File map

| Archivo | Responsabilidad |
|---------|-----------------|
| `kiosk-app/app/src/main/assets/index.html` | UI overlay, `searchCities`, persistencia, wire de eventos |
| `docs/superpowers/specs/2026-09-28-city-selector-design.md` | Spec (ya escrito; no reescribir salvo contradicción) |
| `HANDOFF.md` / `README.md` | Marcar P1.1 hecho al cerrar la feature |

No hay suite de tests automatizada en el repo. Cada task termina con verificación manual concreta (navegador) y, donde haya lógica pura, un smoke `node -e` con asserts.

---

### Task 1: Persistencia lat/lon y arranque preferente

**Files:**
- Modify: `kiosk-app/app/src/main/assets/index.html` (constantes storage + `selectBestLocation` + click 📍)

**Interfaces:**
- Consumes: `STORAGE_CITY_KEY` existente; `geocodeCity`; `detectAutoLocation`
- Produces:
  - `STORAGE_LAT_KEY = "climadesk_manual_lat"`
  - `STORAGE_LON_KEY = "climadesk_manual_lon"`
  - `clearManualCityStorage()` → void (borra city/lat/lon)
  - `saveManualCity(name, lat, lon)` → void
  - `loadSavedManualLocation()` → `{ lat, lon, place, source: "manual" } | null`
  - `selectBestLocation(forceAuto)` usa coords guardadas antes de re-geocodificar por nombre

- [ ] **Step 1: Añadir claves y helpers de storage**

Junto a `STORAGE_CITY_KEY`, añadir:

```javascript
const STORAGE_LAT_KEY = "climadesk_manual_lat";
const STORAGE_LON_KEY = "climadesk_manual_lon";

function clearManualCityStorage() {
  localStorage.removeItem(STORAGE_CITY_KEY);
  localStorage.removeItem(STORAGE_LAT_KEY);
  localStorage.removeItem(STORAGE_LON_KEY);
}

function saveManualCity(name, lat, lon) {
  localStorage.setItem(STORAGE_CITY_KEY, String(name || "").trim());
  localStorage.setItem(STORAGE_LAT_KEY, String(lat));
  localStorage.setItem(STORAGE_LON_KEY, String(lon));
}

function loadSavedManualLocation() {
  const place = (localStorage.getItem(STORAGE_CITY_KEY) || "").trim();
  const lat = Number(localStorage.getItem(STORAGE_LAT_KEY));
  const lon = Number(localStorage.getItem(STORAGE_LON_KEY));
  if (place && Number.isFinite(lat) && Number.isFinite(lon)) {
    return { lat, lon, place, source: "manual" };
  }
  return null;
}
```

- [ ] **Step 2: Actualizar `selectBestLocation`**

Sustituir el cuerpo para que, si no es `forceAuto` y falla GPS, prefiera coords guardadas; si solo hay nombre, `geocodeCity`; si nada, default:

```javascript
async function selectBestLocation(forceAuto) {
  if (forceAuto) return detectAutoLocation();
  try {
    return await detectAutoLocation();
  } catch (_) {
    const saved = loadSavedManualLocation();
    if (saved) return saved;
    const savedCity = (localStorage.getItem(STORAGE_CITY_KEY) || "").trim();
    if (savedCity) return geocodeCity(savedCity);
    return { ...DEFAULT_LOCATION };
  }
}
```

- [ ] **Step 3: Limpiar storage al pulsar 📍**

En el listener de `#locate`, **antes** de `relocalize(true)`:

```javascript
document.getElementById("locate").addEventListener("click", async () => {
  clearManualCityStorage();
  await relocalize(true);
  await loadWeather();
});
```

- [ ] **Step 4: Verificar (navegador o DevTools)**

1. Abrir `kiosk-app/app/src/main/assets/index.html` en Chrome.
2. En consola: `saveManualCity("Madrid", 40.4, -3.7)` luego `loadSavedManualLocation()` → objeto con place Madrid.
3. `clearManualCityStorage()` → `loadSavedManualLocation()` → `null`.
4. Confirmar que el resto de la UI aún abre (el prompt de ciudad sigue existiendo hasta Task 4).

- [ ] **Step 5: Commit (solo si el usuario autoriza)**

```bash
git add kiosk-app/app/src/main/assets/index.html
git commit -m "$(cat <<'EOF'
Persist manual city coordinates for reliable ClimaDesk boot.

EOF
)"
```

---

### Task 2: `formatCityLabel` + `searchCities` + secuencia

**Files:**
- Modify: `kiosk-app/app/src/main/assets/index.html` (cerca de `geocodeCity`)

**Interfaces:**
- Consumes: `fetchJson`
- Produces:
  - `formatCityLabel(name, admin1, country)` → `string`
  - `searchCities(query)` → `Promise<Array<{ lat, lon, name, admin1, country, label }>>`
  - `geocodeCity` puede quedar para fallback por nombre (count=1)

- [ ] **Step 1: Smoke de etiqueta con Node (falla hasta implementar en HTML; copiar la función al one-liner)**

Run:

```bash
node -e "function formatCityLabel(name,admin1,country){return [name,admin1,country].map(function(p){return (p||'').trim();}).filter(Boolean).join(', ');} if(formatCityLabel('Madrid','Comunidad de Madrid','España')!=='Madrid, Comunidad de Madrid, España') process.exit(1); if(formatCityLabel('Paris','','Francia')!=='Paris, Francia') process.exit(1); console.log('ok');"
```

Expected: `ok` (esta es la especificación ejecutable de la etiqueta; la misma función debe vivir en `index.html`).

- [ ] **Step 2: Implementar en `index.html`**

```javascript
function formatCityLabel(name, admin1, country) {
  return [name, admin1, country]
    .map(function (p) { return (p || "").trim(); })
    .filter(Boolean)
    .join(", ");
}

async function searchCities(query) {
  const q = String(query || "").trim();
  if (q.length < 2) return [];
  const url = "https://geocoding-api.open-meteo.com/v1/search"
    + "?name=" + encodeURIComponent(q)
    + "&count=8&language=es";
  const data = await fetchJson(url);
  const rows = (data && data.results) || [];
  return rows.map(function (r) {
    const name = r.name || q;
    const admin1 = r.admin1 || "";
    const country = r.country || "";
    return {
      lat: r.latitude,
      lon: r.longitude,
      name: name,
      admin1: admin1,
      country: country,
      label: formatCityLabel(name, admin1, country)
    };
  });
}
```

- [ ] **Step 3: Verificar búsqueda real**

En la consola del navegador con el HTML abierto (tras recargar):

```javascript
searchCities("Madr").then(function (r) { console.log(r.length, r[0] && r[0].label); })
```

Expected: `length` ≥ 1; primer `label` contiene comas / país o región.

- [ ] **Step 4: Commit (solo si el usuario autoriza)**

```bash
git add kiosk-app/app/src/main/assets/index.html
git commit -m "$(cat <<'EOF'
Add Open-Meteo multi-result city search helpers.

EOF
)"
```

---

### Task 3: Markup y CSS del panel fullscreen

**Files:**
- Modify: `kiosk-app/app/src/main/assets/index.html` (`<style>` + HTML antes de `</body>` / después de `.screen`)

**Interfaces:**
- Consumes: variables CSS existentes (`--bg`, `--fg`, `--muted`, `--accent`, `--card`, `--line`)
- Produces: DOM `#city-picker`, `#city-picker-input`, `#city-picker-list`, `#city-picker-status`, `#city-picker-cancel`

- [ ] **Step 1: Añadir CSS del overlay**

Dentro de `<style>`, al final (antes de media queries si preferís, o después de `.city`):

```css
.city-picker {
  display: none;
  position: fixed;
  inset: 0;
  z-index: 50;
  background: var(--bg);
  color: var(--fg);
  padding: 0.75rem 0.9rem 1rem;
  flex-direction: column;
  gap: 0.65rem;
}
.city-picker.open { display: flex; }
.city-picker-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.75rem;
}
.city-picker-title {
  font-size: 1rem;
  font-weight: 700;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}
.city-picker-cancel {
  border: 1px solid var(--line);
  background: var(--card);
  color: var(--accent);
  border-radius: 0.55rem;
  padding: 0.45rem 0.75rem;
  font-size: 0.9rem;
  cursor: pointer;
}
.city-picker-input {
  width: 100%;
  border: 1px solid var(--line);
  background: var(--card);
  color: var(--fg);
  border-radius: 0.55rem;
  padding: 0.7rem 0.8rem;
  font-size: 1rem;
  -webkit-user-select: text;
  user-select: text;
}
.city-picker-status {
  font-size: 0.85rem;
  color: var(--muted);
  min-height: 1.2rem;
}
.city-picker-list {
  flex: 1;
  overflow: auto;
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
  -webkit-overflow-scrolling: touch;
}
.city-picker-item {
  text-align: left;
  border: 1px solid var(--line);
  background: var(--card);
  color: var(--fg);
  border-radius: 0.55rem;
  padding: 0.75rem 0.8rem;
  font-size: 0.95rem;
  cursor: pointer;
}
.city-picker-item:active { opacity: 0.75; }
```

- [ ] **Step 2: Añadir markup del panel**

Justo **después** de `</div>` de `.screen` (antes del `<script>`):

```html
  <div class="city-picker" id="city-picker" aria-hidden="true">
    <div class="city-picker-head">
      <div class="city-picker-title">Elegir ciudad</div>
      <button type="button" class="city-picker-cancel" id="city-picker-cancel">Cancelar</button>
    </div>
    <input
      class="city-picker-input"
      id="city-picker-input"
      type="search"
      enterkeyhint="search"
      autocomplete="off"
      autocorrect="off"
      spellcheck="false"
      placeholder="Buscar ciudad…"
    />
    <div class="city-picker-status" id="city-picker-status">Escribe para buscar</div>
    <div class="city-picker-list" id="city-picker-list" role="listbox"></div>
  </div>
```

- [ ] **Step 3: Verificar layout**

En consola: `document.getElementById("city-picker").classList.add("open")` → panel cubre pantalla, input y Cancelar visibles, tema día/noche respeta `body.day`. Quitar clase `open` al terminar.

- [ ] **Step 4: Commit (solo si el usuario autoriza)**

```bash
git add kiosk-app/app/src/main/assets/index.html
git commit -m "$(cat <<'EOF'
Add fullscreen city picker chrome for ClimaDesk.

EOF
)"
```

---

### Task 4: Abrir / buscar / elegir / cancelar (quitar `prompt` de ciudad)

**Files:**
- Modify: `kiosk-app/app/src/main/assets/index.html` (reemplazar `chooseCityManually` y listeners)

**Interfaces:**
- Consumes: `searchCities`, `saveManualCity`, `loadWeather`, DOM Task 3
- Produces:
  - `openCityPicker()` / `closeCityPicker()`
  - `renderCityPickerResults(items)`
  - `chooseCityManually()` abre el panel (ya no usa `prompt`)
  - Variable `citySearchSeq` (number) para ignorar respuestas viejas
  - Debounce timer ~300 ms en `input` del picker

- [ ] **Step 1: Implementar control del panel**

Reemplazar `chooseCityManually` y añadir:

```javascript
let citySearchSeq = 0;
let citySearchTimer = null;

function setCityPickerStatus(text) {
  document.getElementById("city-picker-status").textContent = text || "";
}

function renderCityPickerResults(items) {
  const host = document.getElementById("city-picker-list");
  host.innerHTML = "";
  (items || []).forEach(function (item) {
    const btn = document.createElement("button");
    btn.type = "button";
    btn.className = "city-picker-item";
    btn.setAttribute("role", "option");
    btn.textContent = item.label;
    btn.addEventListener("click", function () {
      saveManualCity(item.name, item.lat, item.lon);
      currentLocation = {
        lat: item.lat,
        lon: item.lon,
        place: item.name,
        source: "manual"
      };
      document.getElementById("city").textContent = item.name;
      closeCityPicker();
      loadWeather();
    });
    host.appendChild(btn);
  });
}

function closeCityPicker() {
  const panel = document.getElementById("city-picker");
  panel.classList.remove("open");
  panel.setAttribute("aria-hidden", "true");
  if (citySearchTimer) {
    clearTimeout(citySearchTimer);
    citySearchTimer = null;
  }
  citySearchSeq += 1;
}

function openCityPicker() {
  const panel = document.getElementById("city-picker");
  const input = document.getElementById("city-picker-input");
  panel.classList.add("open");
  panel.setAttribute("aria-hidden", "false");
  input.value = localStorage.getItem(STORAGE_CITY_KEY) || "";
  renderCityPickerResults([]);
  setCityPickerStatus("Escribe para buscar");
  input.focus();
  if (input.value.trim().length >= 2) runCitySearch(input.value);
}

async function runCitySearch(raw) {
  const q = String(raw || "").trim();
  const seq = ++citySearchSeq;
  if (q.length < 2) {
    renderCityPickerResults([]);
    setCityPickerStatus("Escribe para buscar");
    return;
  }
  setCityPickerStatus("Buscando…");
  try {
    const items = await searchCities(q);
    if (seq !== citySearchSeq) return;
    if (!items.length) {
      renderCityPickerResults([]);
      setCityPickerStatus("Sin resultados");
      return;
    }
    renderCityPickerResults(items);
    setCityPickerStatus(items.length + " resultado" + (items.length === 1 ? "" : "s"));
  } catch (_) {
    if (seq !== citySearchSeq) return;
    renderCityPickerResults([]);
    setCityPickerStatus("No se pudo buscar");
  }
}

function chooseCityManually() {
  openCityPicker();
}
```

- [ ] **Step 2: Wire eventos del panel**

Tras crear listeners existentes:

```javascript
document.getElementById("city-picker-cancel").addEventListener("click", closeCityPicker);
document.getElementById("city-picker-input").addEventListener("input", function (e) {
  const value = e.target.value;
  if (citySearchTimer) clearTimeout(citySearchTimer);
  citySearchTimer = setTimeout(function () {
    runCitySearch(value);
  }, 300);
});
```

Confirmar que `#city` sigue llamando `chooseCityManually` (ya abre el panel).

Asegurar que **no queda** `window.prompt` en `chooseCityManually` (el prompt de escala UI en la fecha puede permanecer).

- [ ] **Step 3: Checklist de aceptación (navegador)**

1. Tocar nombre de ciudad → panel fullscreen.
2. Escribir `Madr` → varias filas con región/país; estado no se queda en «Buscando…».
3. Elegir una → panel cierra; `#city` = nombre corto; clima carga; `localStorage` tiene city/lat/lon.
4. Recargar página → tras GPS fallido o sin permiso, usa coords guardadas (o al menos ciudad manual).
5. Abrir panel y Cancelar → ubicación y storage intactos.
6. 📍 → limpia city/lat/lon y fuerza GPS (o error GPS + default).
7. Buscar con red cortada (DevTools Offline) → «No se pudo buscar».

- [ ] **Step 4: Commit (solo si el usuario autoriza)**

```bash
git add kiosk-app/app/src/main/assets/index.html
git commit -m "$(cat <<'EOF'
Replace city prompt with live Open-Meteo search picker.

EOF
)"
```

---

### Task 5: Documentación de estado (roadmap / handoff)

**Files:**
- Modify: `HANDOFF.md`
- Modify: `README.md` (tabla Roadmap)

**Interfaces:**
- Consumes: feature ya verificada en Task 4
- Produces: P1.1 marcado como hecho; siguiente sesión = P1.2 `applicationId`

- [ ] **Step 1: Actualizar `HANDOFF.md`**

- Fecha de sesión → hoy.
- En **Hecho**: añadir «Selector de ciudad con búsqueda (sin prompt)».
- En **Pendiente**: quitar el ítem del selector; dejar P1.2 package + P2 icono como siguientes.
- En tabla Roadmap: P1 selector → hecho / ✅.

- [ ] **Step 2: Actualizar `README.md` Roadmap**

Fila «Selector de ciudad…» → ✅; «Siguiente» pasa a `applicationId` ClimaDesk.

- [ ] **Step 3: Commit (solo si el usuario autoriza)**

```bash
git add HANDOFF.md README.md
git commit -m "$(cat <<'EOF'
Mark city selector P1.1 done in handoff and roadmap.

EOF
)"
```

---

## Spec coverage (self-review)

| Requisito spec | Task |
|----------------|------|
| Sugerencias en vivo, debounce 300 ms, ≥2 chars, count=8, es | 2, 4 |
| Label nombre/admin1/país | 2 |
| Panel fullscreen, cancelar, sin GPS en panel | 3, 4 |
| Persist city + lat/lon; clear en 📍 | 1, 4 |
| `#city` nombre corto | 4 |
| Secuencia anti-carrera (no AbortController) | 4 |
| Errores panel / clima | 4 |
| Sin APK / solo index.html | Global + tasks |
| Docs roadmap | 5 |

No quedan placeholders TBD/TODO en los steps.
