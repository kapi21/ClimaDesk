# Design: Kiosk duro — autoarranque + launcher (P3-A)

Fecha: 2026-09-28  
Proyecto: ClimaDesk  
Estado: aprobado en conversación; pendiente de plan de implementación

## Objetivo

Permitir que el Redmi 4X se comporte como aparato de mesa: ClimaDesk puede ser **launcher** (Home) y **arrancar al boot**, sin Device Admin ni UI de conmutador. La activación real depende de elecciones del usuario en Android/MIUI (reversible).

## Contexto P3

| Ciclo | Contenido | Orden |
|-------|-----------|-------|
| **P3-A (este)** | Kiosk duro: HOME + BOOT | Primero |
| **P3-B (después)** | Release firmada + tag GitHub | Segundo |
| P2 | Modo solo-reloj | Aplazado |

## Decisiones

| Tema | Elección |
|------|----------|
| Alcance | Autoarranque + poder ser launcher |
| Activación | Siempre declarado en manifiesto; usuario elige launcher / Autostart MIUI |
| Docs de uso/deshacer | Solo HANDOFF + README |
| Implementación | Manifest + `BootReceiver` mínimo en `MainActivity` package |
| Versión | `1.8` / `versionCode` 9 |
| Brillo / Device Admin / toggle in-app | Fuera de alcance |

## Cambios técnicos

### Manifest (`AndroidManifest.xml`)

1. Permiso:
   - `android.permission.RECEIVE_BOOT_COMPLETED`
2. `MainActivity` intent-filters:
   - Existente: `MAIN` + `LAUNCHER`
   - Añadir (mismo activity): `MAIN` + `HOME` + `DEFAULT` (para aparecer en el selector de launcher)
3. Receiver:
   - `es.climadesk.app.BootReceiver`
   - `android:exported="true"` (requerido para receivers con intent-filter en API modernas; en target 25 también correcto)
   - Intent-filter: `android.intent.action.BOOT_COMPLETED`
   - Opcional documentado: no añadir acciones manufacturer-specific salvo que falle el boot en el Redmi en pruebas

### Java — `BootReceiver.java`

```text
onReceive → startActivity(MainActivity) con FLAG_ACTIVITY_NEW_TASK
```

Sin preferencias, sin comprobaciones de “modo kiosk”, sin servicios en foreground.

### Gradle

- `versionName` = `1.8`
- `versionCode` = `9`

### Sin cambios

- `index.html` / WebView
- Icono, package id
- Brillo del sistema

## Documentación

Sección breve en `HANDOFF.md` y `README.md`:

**Activar**
1. Instalar / actualizar ClimaDesk.
2. MIUI: Seguridad / Autostart → permitir ClimaDesk.
3. Pulsar Home → elegir ClimaDesk → “Siempre”.

**Quitar**
1. Ajustes → Apps → ClimaDesk → Abrir de forma predeterminada → borrar defaults (o elegir launcher del sistema al pulsar Home).
2. Desactivar Autostart de ClimaDesk.
3. O desinstalar la app.

## Fuera de alcance

- Control de brillo
- Device Admin / lock task / kiosk MDM
- Pantalla de ayuda in-app
- Release firmada (P3-B)
- Modo solo-reloj (P2)

## Verificación

1. `gradlew.bat :app:assembleDebug` → SUCCESS  
2. En Redmi (API 25): tras instalar, Home muestra diálogo de launcher con ClimaDesk.  
3. Con Autostart MIUI ON: reiniciar → abre ClimaDesk (si MIUI no lo bloquea).  
4. Borrar defaults → Home vuelve al launcher sistema.

## Criterios de éxito

- La app puede seleccionarse como launcher.
- Existe camino de boot → MainActivity (sujeto a Autostart MIUI).
- Docs explican activar y deshacer.
- No se introduce Device Admin ni brillo forzado.
- Versión 1.8 / 9.
