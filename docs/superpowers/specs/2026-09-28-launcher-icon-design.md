# Design: Icono launcher ClimaDesk (P2)

Fecha: 2026-09-28  
Proyecto: ClimaDesk  
Estado: aprobado; APK debug autorizado para prueba en dispositivo

## Objetivo

Sustituir el icono genérico de reloj por un vector flat de **reloj + sol**, con adaptive icon en API 26+ y fallback vector en API ≤25 (Redmi 4X).

## Decisiones

| Tema | Elección |
|------|----------|
| Concepto | Reloj + clima (sol) |
| Estilo | Flat; colores `#0B1020`, `#7EB8FF`, `#F4F1EA`, sol `#F4C96A` |
| Formato | Vector + adaptive (`mipmap-anydpi-v26`) + legacy `mipmap-anydpi` |
| Manifest | `android:icon="@mipmap/ic_launcher"` |
| Versión | Sin bump (sigue 1.7 / 8) |
| APK | `assembleDebug` autorizado para probar en dispositivo |

## Recursos

| Path | Rol |
|------|-----|
| `res/drawable/ic_launcher_background.xml` | Fondo adaptive |
| `res/drawable/ic_launcher_foreground.xml` | Reloj + sol (safe zone ~66%) |
| `res/mipmap-anydpi-v26/ic_launcher.xml` | Adaptive API 26+ |
| `res/mipmap-anydpi/ic_launcher.xml` | Vector compuesto API ≤25 |
| Eliminar o dejar de referenciar | `res/drawable/ic_launcher.xml` antiguo si el manifest ya no lo usa |

## Docs

HANDOFF/README: P2 icono ✅; siguiente pendiente del roadmap (modo solo-reloj).

## Verificación

1. `gradlew.bat :app:assembleDebug` → BUILD SUCCESSFUL  
2. APK en `kiosk-app/app/build/outputs/apk/debug/app-debug.apk`  
3. Instalar en Redmi (push a Download si MIUI bloquea adb install)
