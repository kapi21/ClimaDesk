# Lite Sun Stack Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** En ClimaDesk Lite, apilar AMANECER encima de ATARDECER, bump a `1.8.1-lite` y generar `Climadesk-Lite-v1.8.1.apk`.

**Architecture:** Cambio mínimo de CSS en `.left-section-bottom` (`flex-direction: column`). HTML y JS intactos. Código en rama `redmi4x-lite`; docs y carpeta `apks/` se actualizan en `main`.

**Tech Stack:** HTML/CSS en assets WebView, Gradle Kotlin DSL, `gradlew.bat assembleDebug`, adb en Windows PowerShell.

## Global Constraints

- Solo rama de código `redmi4x-lite` para `index.html` y `build.gradle.kts`
- `versionName` = `1.8.1-lite`, `versionCode` = `11`
- Layout interno de cada `.sun-time`: sin cambios (icono + etiqueta + hora)
- Sin tocar ramas Tablet, JS, Open-Meteo ni IDs `txt-sunrise` / `txt-sunset`
- GitHub Release fuera de este plan
- Commits solo si el usuario autoriza
- Rutas con espacios: siempre entre comillas (`"C:\@MIS PROYECTOS\ClimaDesk"`)

## File map

| Archivo | Rama | Acción |
|---------|------|--------|
| `kiosk-app/app/src/main/assets/index.html` | `redmi4x-lite` | CSS de `.left-section-bottom` |
| `kiosk-app/app/build.gradle.kts` | `redmi4x-lite` | `1.8.1-lite` / `11` |
| `apks/Climadesk-Lite-v1.8.1.apk` | local (+ docs en `main`) | Copiar APK compilado |
| `apks/README.md` | `main` | Nombre Lite → v1.8.1 |
| `HANDOFF.md` | `main` | Versión, APK, adb |
| `README.md` | `main` | Texto Lite (enlace Release puede seguir a v1.8 hasta nueva release) |
| Spec | `main` | `docs/superpowers/specs/2026-10-30-lite-sun-stack-design.md` (ya committed) |

---

### Task 1: CSS + versión en `redmi4x-lite`

**Files:**
- Modify: `kiosk-app/app/src/main/assets/index.html` (bloque `.left-section-bottom`)
- Modify: `kiosk-app/app/build.gradle.kts` (`defaultConfig`)

**Interfaces:**
- Consumes: markup existente `.left-section-bottom` > dos `.sun-time` (amanecer primero)
- Produces: columna vertical; app versionada `1.8.1-lite` / 11

- [ ] **Step 1: Checkout de la rama Lite**

```powershell
cd "C:\@MIS PROYECTOS\ClimaDesk"
git status
git checkout redmi4x-lite
```

Expected: `On branch redmi4x-lite`, working tree limpio (o solo cambios intencionados).

- [ ] **Step 2: Verificar estado actual (antes del cambio)**

```powershell
Select-String -Path "kiosk-app\app\src\main\assets\index.html" -Pattern "left-section-bottom" -Context 0,7
Select-String -Path "kiosk-app\app\build.gradle.kts" -Pattern "versionCode|versionName"
```

Expected: `justify-content: space-around;` (sin `flex-direction: column`); `versionCode = 10`; `versionName = "1.8-lite"`.

- [ ] **Step 3: Sustituir el bloque CSS `.left-section-bottom`**

Reemplazar exactamente:

```css
    .left-section-bottom {
      padding: 6px 10px;
      display: flex;
      align-items: center;
      justify-content: space-around;
      flex: 0.75;
      background-color: var(--tile-bg);
    }
```

por:

```css
    .left-section-bottom {
      padding: 6px 10px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 6px;
      flex: 0.75;
      background-color: var(--tile-bg);
    }
```

No modificar el HTML de `.sun-time` ni `body.night .left-section-bottom`.

- [ ] **Step 4: Bump de versión en Gradle**

En `kiosk-app/app/build.gradle.kts`, dentro de `defaultConfig`:

```kotlin
        versionCode = 11
        versionName = "1.8.1-lite"
```

- [ ] **Step 5: Verificar el diff**

```powershell
Select-String -Path "kiosk-app\app\src\main\assets\index.html" -Pattern "flex-direction: column|gap: 6px|justify-content: center"
Select-String -Path "kiosk-app\app\build.gradle.kts" -Pattern "versionCode|versionName"
```

Expected: CSS con `column` + `gap: 6px` + `center`; `versionCode = 11`; `versionName = "1.8.1-lite"`.

- [ ] **Step 6: Commit (solo si el usuario autoriza)**

```powershell
git add kiosk-app/app/src/main/assets/index.html kiosk-app/app/build.gradle.kts
git commit -m "fix(lite): stack sunrise/sunset vertically and bump to 1.8.1-lite"
```

---

### Task 2: Compilar APK Lite e instalar en Redmi

**Files:**
- Create (local, gitignored): `apks/Climadesk-Lite-v1.8.1.apk`
- Optional remove/replace: `apks/Climadesk-Lite-v1.8.apk` (dejar el viejo o sustituir; el README apuntará al nuevo)

**Interfaces:**
- Consumes: código Task 1 en `redmi4x-lite`
- Produces: APK instalable con layout apilado

- [ ] **Step 1: Compilar debug**

```powershell
cd "C:\@MIS PROYECTOS\ClimaDesk\kiosk-app"
.\gradlew.bat assembleDebug
```

Expected: `BUILD SUCCESSFUL`. Artefacto:
`kiosk-app\app\build\outputs\apk\debug\app-debug.apk`

- [ ] **Step 2: Copiar a `apks/` con nombre limpio**

```powershell
cd "C:\@MIS PROYECTOS\ClimaDesk"
Copy-Item "kiosk-app\app\build\outputs\apk\debug\app-debug.apk" "apks\Climadesk-Lite-v1.8.1.apk" -Force
Get-Item "apks\Climadesk-Lite-v1.8.1.apk" | Select-Object Name, Length, LastWriteTime
```

Expected: archivo presente, tamaño > 0, fecha reciente.

- [ ] **Step 3: Instalar en Redmi 4X**

```powershell
adb devices
adb -s 6f207ef7d440 install -r "apks\Climadesk-Lite-v1.8.1.apk"
```

Expected: `Success`. Si el dispositivo no está, parar e informar; no inventar otro serial.

- [ ] **Step 4: Comprobación visual (checklist)**

Abrir ClimaDesk en el Redmi y verificar:

1. AMANECER arriba, ATARDECER abajo
2. Cada fila sigue con icono + etiqueta + hora
3. El reloj no queda aplastado de forma inaceptable

Si el reloj se aplasta: ajustar `flex` de `.left-section-top` / `.left-section-bottom` (contingencia de la spec) en un commit aparte autorizado.

---

### Task 3: Docs en `main`

**Files:**
- Modify: `apks/README.md`
- Modify: `HANDOFF.md`
- Modify: `README.md` (línea de descarga Lite / mención de versión)

**Interfaces:**
- Consumes: APK `Climadesk-Lite-v1.8.1.apk` y versión `1.8.1-lite` / 11
- Produces: docs alineados con el binario local

- [ ] **Step 1: Volver a `main` y traer el APK si hace falta**

```powershell
cd "C:\@MIS PROYECTOS\ClimaDesk"
git checkout main
```

El APK en `apks/` suele estar gitignored: debe seguir en disco tras el checkout. Confirmar:

```powershell
Test-Path "apks\Climadesk-Lite-v1.8.1.apk"
```

Expected: `True`.

- [ ] **Step 2: Actualizar `apks/README.md`**

Cambiar la fila Lite de la tabla a:

| `Climadesk-Lite-v1.8.1.apk` | Xiaomi Redmi 4X | Edición Lite ultra-ligera (640x360 dp; AMANECER/ATARDECER en columna) |

- [ ] **Step 3: Actualizar `HANDOFF.md`**

Actualizar al menos:

- Referencia de APK Lite → `apks/Climadesk-Lite-v1.8.1.apk`
- Versión Redmi → `1.8.1-lite` (`versionCode` 11)
- Comando adb de ejemplo con el nuevo nombre de archivo
- Nota breve en “Hecho” / estado: layout solar vertical en Lite

- [ ] **Step 4: Actualizar `README.md`**

En la lista de descargas Lite, cambiar el nombre visible a `Climadesk-Lite-v1.8.1.apk`.  
Si el enlace de GitHub Releases aún apunta a `v1.8` / `Climadesk-Lite-v1.8.apk`, dejar el href de Release como está (release nueva fuera de alcance) y aclarar en texto que el binario local/carpeta `apks/` es `v1.8.1`, **o** apuntar solo a la carpeta `apks/` para Lite hasta nueva release. Preferencia: mantener enlace Release v1.8 y añadir mención de `apks/Climadesk-Lite-v1.8.1.apk` como build local más reciente.

- [ ] **Step 5: Commit docs (solo si el usuario autoriza)**

```powershell
git add apks/README.md HANDOFF.md README.md
git commit -m "docs: Lite v1.8.1 with stacked sunrise/sunset"
```

---

## Contingencia (solo si falla la checklist visual)

Si tras instalar el reloj pierde demasiada altura, en `redmi4x-lite` ajustar proporciones, por ejemplo:

```css
.left-section-top { flex: 1.35; }
.left-section-bottom { flex: 0.65; }
```

(valores exactos a calibrar en dispositivo; no aplicar preventivamente).

---

## Spec coverage (self-review)

| Requisito spec | Task |
|----------------|------|
| CSS column + center + gap | Task 1 |
| Sin cambiar HTML/JS filas | Task 1 (explícito) |
| Solo `redmi4x-lite` para código | Task 1–2 |
| versionName `1.8.1-lite` / code 11 | Task 1 |
| APK `Climadesk-Lite-v1.8.1.apk` | Task 2 |
| Docs `apks/README`, HANDOFF, README | Task 3 |
| Sin GitHub Release / sin Tablet | Global Constraints |
| Verificación en Redmi | Task 2 Step 3–4 |
| Contingencia flex | Sección Contingencia |
