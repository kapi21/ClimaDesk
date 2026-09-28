# Launcher Icon (P2) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Icono launcher flat reloj+sol con adaptive (API 26+) y fallback vector (API ≤25); APK debug para prueba.

**Architecture:** Foreground/background drawables + `mipmap-anydpi-v26` adaptive + `mipmap-anydpi` composite; manifest `@mipmap/ic_launcher`.

**Tech Stack:** Android VectorDrawable / AdaptiveIconDrawable XML.

## Global Constraints

- Colores: fondo `#0B1020`, acento `#7EB8FF`, crema `#F4F1EA`, sol `#F4C96A`
- Canvas 108×108; arte en safe zone ~66%
- Sin bump de versión (1.7 / 8)
- `assembleDebug` OK; instalar solo para prueba según usuario
- Commits solo si el usuario autoriza

---

### Task 1: Recursos de icono + manifest

**Files:**
- Create: `kiosk-app/app/src/main/res/drawable/ic_launcher_background.xml`
- Create: `kiosk-app/app/src/main/res/drawable/ic_launcher_foreground.xml`
- Create: `kiosk-app/app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`
- Create: `kiosk-app/app/src/main/res/mipmap-anydpi/ic_launcher.xml`
- Modify: `kiosk-app/app/src/main/AndroidManifest.xml` → `android:icon="@mipmap/ic_launcher"`
- Delete or stop using: `kiosk-app/app/src/main/res/drawable/ic_launcher.xml` (legacy clock-only)

- [ ] **Step 1: Background**

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#0B1020"
        android:pathData="M0,0h108v108h-108z" />
</vector>
```

- [ ] **Step 2: Foreground (reloj + sol, centrado)**

Vector 108×108: círculo centro (54,54) radio ~22; manecillas; sol en (70,38) disco r~7 + rayos cortos. Stroke caps round. Sin texto.

- [ ] **Step 3: Adaptive v26**

```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>
```

- [ ] **Step 4: Legacy mipmap-anydpi**

Layer-list o vector compuesto equivalente (fondo + mismo arte del foreground) en `mipmap-anydpi/ic_launcher.xml`.

- [ ] **Step 5: Manifest + borrar drawable viejo**

`android:icon="@mipmap/ic_launcher"`; eliminar `drawable/ic_launcher.xml` obsoleto.

- [ ] **Step 6: Build APK**

```bat
cd kiosk-app
gradlew.bat :app:assembleDebug
```

Expected: `app/build/outputs/apk/debug/app-debug.apk`

---

### Task 2: Docs

- Update HANDOFF.md / README.md roadmap (icono ✅)
- Commit solo si el usuario autoriza
