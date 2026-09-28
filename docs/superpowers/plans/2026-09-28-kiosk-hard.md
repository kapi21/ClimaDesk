# Kiosk Hard Mode P3-A Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Declarar ClimaDesk como launcher opcional y arrancar `MainActivity` tras `BOOT_COMPLETED`, con docs de activar/deshacer y bump a v1.8.

**Architecture:** Cambios mínimos en AndroidManifest + `BootReceiver` Java. Sin UI, sin Device Admin, sin preferencias. La activación real la hace el usuario en MIUI (Autostart + elegir launcher).

**Tech Stack:** Android Java Activity/BroadcastReceiver, manifest intent-filters, Gradle versionCode/Name.

## Global Constraints

- Package / namespace: `es.climadesk.app` (sin cambiar)
- `versionName` = `1.8`, `versionCode` = `9`
- HOME + LAUNCHER en `MainActivity`; `BootReceiver` → `MainActivity` con `FLAG_ACTIVITY_NEW_TASK`
- Sin Device Admin, sin brillo, sin toggle in-app, sin tocar `index.html`
- Docs solo HANDOFF + README (checklist activar/quitar)
- P3-B release firmada y P2 solo-reloj fuera de este plan
- Commits solo si el usuario autoriza
- APK: `assembleDebug` OK; instalar solo si el usuario lo pide

## File map

| Archivo | Acción |
|---------|--------|
| `kiosk-app/app/src/main/AndroidManifest.xml` | Permiso boot, HOME filter, receiver |
| `kiosk-app/app/src/main/java/es/climadesk/app/BootReceiver.java` | Crear |
| `kiosk-app/app/build.gradle.kts` | 1.8 / 9 |
| `HANDOFF.md`, `README.md` | Checklist kiosk + roadmap |
| Spec | `docs/superpowers/specs/2026-09-28-kiosk-hard-design.md` (ya existe) |

---

### Task 1: Manifest + BootReceiver + versión

**Files:**
- Modify: `kiosk-app/app/src/main/AndroidManifest.xml`
- Create: `kiosk-app/app/src/main/java/es/climadesk/app/BootReceiver.java`
- Modify: `kiosk-app/app/build.gradle.kts`

**Interfaces:**
- Consumes: `MainActivity` existente
- Produces: boot → MainActivity; app eligible as HOME launcher; version 1.8/9

- [ ] **Step 1: Actualizar `build.gradle.kts`**

```kotlin
        versionCode = 9
        versionName = "1.8"
```

- [ ] **Step 2: Actualizar `AndroidManifest.xml`**

Tras los permisos de ubicación, añadir:

```xml
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

En `MainActivity`, dejar el intent-filter LAUNCHER y añadir un segundo intent-filter:

```xml
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.HOME" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
```

Antes de `</application>`, registrar:

```xml
        <receiver
            android:name=".BootReceiver"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
        </receiver>
```

- [ ] **Step 3: Crear `BootReceiver.java`**

```java
package es.climadesk.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        String action = intent.getAction();
        if (action == null) return;
        if (!Intent.ACTION_BOOT_COMPLETED.equals(action)) return;

        Intent launch = new Intent(context, MainActivity.class);
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launch);
    }
}
```

- [ ] **Step 4: Compilar**

```bat
cd kiosk-app
gradlew.bat :app:assembleDebug
```

Expected: `BUILD SUCCESSFUL`. APK en `app/build/outputs/apk/debug/app-debug.apk`.

- [ ] **Step 5: Commit (solo si el usuario autoriza)**

```bash
git add kiosk-app/app/build.gradle.kts kiosk-app/app/src/main/AndroidManifest.xml \
  kiosk-app/app/src/main/java/es/climadesk/app/BootReceiver.java
git commit -m "Add boot receiver and HOME launcher support (v1.8)."
```

---

### Task 2: Documentación HANDOFF / README

**Files:**
- Modify: `HANDOFF.md`
- Modify: `README.md`
- Ensure: `docs/superpowers/specs/2026-09-28-kiosk-hard-design.md` y este plan en el commit si se autoriza

**Interfaces:**
- Consumes: Task 1
- Produces: checklist activar/quitar; roadmap P3 kiosk ✅ (release firmada sigue pendiente)

- [ ] **Step 1: `HANDOFF.md`**

- Versión → `1.8` (`versionCode` 9)
- Hecho: autoarranque + HOME launcher (sujeto a MIUI)
- Sección **Kiosk duro (activar / quitar)** con los pasos del spec
- Roadmap: fila P3 kiosk duro → ✅; P3 release firmada sigue pendiente
- Pendiente siguiente: release firmada (P3-B) y/o modo solo-reloj (P2)

- [ ] **Step 2: `README.md`**

- Añadir apartado corto “Usar como reloj de mesa (kiosk)” o ampliar uso rápido con los 3 pasos activar + cómo quitar
- Roadmap: Hecho kiosk duro v1.8; Siguiente = release firmada o solo-reloj (indicar ambos como pendientes)

- [ ] **Step 3: Commit (solo si el usuario autoriza)**

```bash
git add HANDOFF.md README.md docs/superpowers/specs/2026-09-28-kiosk-hard-design.md \
  docs/superpowers/plans/2026-09-28-kiosk-hard.md
git commit -m "Document hard-kiosk activate and undo steps."
```

---

## Spec coverage (self-review)

| Requisito | Task |
|-----------|------|
| RECEIVE_BOOT_COMPLETED + BootReceiver | 1 |
| HOME + DEFAULT en MainActivity | 1 |
| version 1.8 / 9 | 1 |
| Docs activar/quitar | 2 |
| Sin Device Admin / brillo / UI toggle | Global |
| P3-B y P2 fuera | Global |
