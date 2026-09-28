# Package Rename to es.climadesk.app (P1.2) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Renombrar la identidad Android a `es.climadesk.app` (Gradle + Java + theme/proyecto) y subir a versión 1.7 / versionCode 8.

**Architecture:** Movimiento físico de `MainActivity` al nuevo path de package, actualización de `namespace`/`applicationId`, rename de theme y `rootProject.name`, y docs. Sin cambios de comportamiento en WebView ni assets.

**Tech Stack:** Android Gradle Plugin (Kotlin DSL), Java Activity + WebView, recursos XML.

## Global Constraints

- `applicationId` = `es.climadesk.app`
- `namespace` = `es.climadesk.app`
- Java `package` = `es.climadesk.app`
- `versionName` = `1.7`, `versionCode` = `8`
- Theme = `Theme.ClimaDesk`; `rootProject.name` = `ClimaDesk`
- No empaquetar/instalar APK salvo petición explícita del usuario
- Commits: solo si el usuario los autoriza en la sesión
- No migrar datos/`localStorage` entre packages

## File map

| Archivo | Acción |
|---------|--------|
| `kiosk-app/app/build.gradle.kts` | namespace, applicationId, versión |
| `kiosk-app/settings.gradle.kts` | rootProject.name |
| `kiosk-app/app/src/main/java/es/climadesk/app/MainActivity.java` | Crear |
| `kiosk-app/app/src/main/java/es/redmi4x/relojclima/MainActivity.java` | Eliminar |
| `kiosk-app/app/src/main/res/values/styles.xml` | Theme rename |
| `kiosk-app/app/src/main/AndroidManifest.xml` | Theme refs |
| `HANDOFF.md`, `README.md` | Roadmap / package docs |
| Spec ya escrita | `docs/superpowers/specs/2026-09-28-package-rename-design.md` (incluir en commit si se autoriza) |

---

### Task 1: Gradle, Java path, theme

**Files:**
- Modify: `kiosk-app/app/build.gradle.kts`
- Modify: `kiosk-app/settings.gradle.kts`
- Create: `kiosk-app/app/src/main/java/es/climadesk/app/MainActivity.java`
- Delete: `kiosk-app/app/src/main/java/es/redmi4x/relojclima/MainActivity.java` (+ dirs vacías)
- Modify: `kiosk-app/app/src/main/res/values/styles.xml`
- Modify: `kiosk-app/app/src/main/AndroidManifest.xml`

**Interfaces:**
- Consumes: contenido actual de `MainActivity` (solo cambia la línea `package`)
- Produces: app identity `es.climadesk.app` compilable

- [ ] **Step 1: Actualizar `build.gradle.kts`**

```kotlin
android {
    namespace = "es.climadesk.app"
    // ...
    defaultConfig {
        applicationId = "es.climadesk.app"
        minSdk = 21
        targetSdk = 25
        versionCode = 8
        versionName = "1.7"
    }
```

- [ ] **Step 2: Actualizar `settings.gradle.kts`**

```kotlin
rootProject.name = "ClimaDesk"
```

- [ ] **Step 3: Crear MainActivity en el nuevo path**

Crear directorios `es/climadesk/app/`. Copiar el archivo actual cambiando **solo**:

```java
package es.climadesk.app;
```

Resto del archivo idéntico (WebView, permisos, immersive).

- [ ] **Step 4: Eliminar el package viejo**

Borrar `kiosk-app/app/src/main/java/es/redmi4x/relojclima/MainActivity.java` y las carpetas vacías `relojclima`, `redmi4x` si quedan sin archivos.

- [ ] **Step 5: Renombrar theme**

En `styles.xml`:

```xml
<style name="Theme.ClimaDesk" parent="@android:style/Theme.DeviceDefault.NoActionBar.Fullscreen">
```

En `AndroidManifest.xml`, ambas apariciones de `Theme.RelojClima` → `Theme.ClimaDesk`.

- [ ] **Step 6: Verificar literales**

Desde la raíz del repo (PowerShell):

```powershell
Select-String -Path "kiosk-app\**\*.*" -Pattern "es\.redmi4x\.relojclima|Theme\.RelojClima|RelojClimaKiosk" -SimpleMatch:$false
```

Expected: **cero** matches en fuentes bajo `kiosk-app/` (ignorar `build/` si aparece).

Opcional compile (no instalar):

```bat
cd kiosk-app
gradlew.bat :app:assembleDebug
```

Expected: `BUILD SUCCESSFUL`. No hacer `adb install` ni release.

- [ ] **Step 7: Commit (solo si el usuario autoriza)**

```bash
git add kiosk-app/app/build.gradle.kts kiosk-app/settings.gradle.kts \
  kiosk-app/app/src/main/java/es/climadesk/app/MainActivity.java \
  kiosk-app/app/src/main/res/values/styles.xml \
  kiosk-app/app/src/main/AndroidManifest.xml
git add -u kiosk-app/app/src/main/java/es/redmi4x/
git commit -m "Rename Android package to es.climadesk.app (v1.7)."
```

---

### Task 2: Docs HANDOFF / README + spec en árbol

**Files:**
- Modify: `HANDOFF.md`
- Modify: `README.md`
- Ensure present: `docs/superpowers/specs/2026-09-28-package-rename-design.md`
- Ensure present: `docs/superpowers/plans/2026-09-28-package-rename.md` (este plan)

**Interfaces:**
- Consumes: Task 1 completada
- Produces: roadmap con P1.2 ✅; siguiente = icono P2

- [ ] **Step 1: Actualizar `HANDOFF.md`**

- Versión → `1.7` (`versionCode` 8) — package `es.climadesk.app`
- Hecho: añadir rename package / applicationId
- Pendiente: quitar rename; dejar icono como #1
- Archivos clave: path Java → `…/java/es/climadesk/app/MainActivity.java`
- Roadmap fila P1 applicationId → ✅
- Nota breve: reinstalar; desinstalar id viejo si no quieres dos apps

- [ ] **Step 2: Actualizar `README.md` Roadmap**

- Fila applicationId → ✅ (o nueva fila Hecho)
- Siguiente → Icono propio / branding
- Si menciona versión en tabla “Hecho” del kiosk base, puede quedar v1.6 histórico o actualizar a “hasta v1.7”; preferir no reescribir historia: añadir fila Hecho para package rename v1.7

- [ ] **Step 3: Verificar docs**

```powershell
Select-String -Path HANDOFF.md,README.md -Pattern "es\.redmi4x\.relojclima"
```

Expected: sin matches (o solo en nota histórica de desinstalación si se menciona el id viejo a propósito — preferible una frase: “Desinstalar la app antigua `es.redmi4x.relojclima` si sigue instalada”).

- [ ] **Step 4: Commit (solo si el usuario autoriza)**

```bash
git add HANDOFF.md README.md docs/superpowers/specs/2026-09-28-package-rename-design.md docs/superpowers/plans/2026-09-28-package-rename.md
git commit -m "Document package rename to es.climadesk.app in handoff and roadmap."
```

---

## Spec coverage (self-review)

| Requisito spec | Task |
|----------------|------|
| applicationId/namespace/Java `es.climadesk.app` | 1 |
| version 1.7 / 8 | 1 |
| Theme.ClimaDesk + rootProject ClimaDesk | 1 |
| Eliminar package viejo | 1 |
| Docs + breaking install note | 2 |
| Sin APK forzada | Global |
| Sin migración localStorage | Global (no code) |
