# Design: Atenuación gradual por amanecer / atardecer

Fecha: 2026-11-01  
Proyecto: ClimaDesk  
Estado: aprobado en conversación; pendiente de plan de implementación

## Objetivo

Atenuar visualmente la UI de ClimaDesk de forma **gradual** según los horarios reales de **amanecer y atardecer** de la ubicación actual, y ofrecer un **botón superior de test** que simule un ciclo completo acelerado.

## Decisiones

| Tema | Elección |
|------|----------|
| Mecanismo | Overlay negro semitransparente (no brillo del sistema) |
| Ciclo | Tras atardecer baja a mínimo nocturno; al amanecer sube a pleno |
| Duración de cada rampa | ~30 minutos |
| Nivel nocturno | ~70 % visible → opacidad del overlay `0.30` |
| Horarios solares | Open-Meteo `daily.sunrise` / `daily.sunset` |
| Botón test | Simulación completa día→noche→día en ~12 s, luego modo real |
| Alcance | Las tres variantes: base/`main`, `redmi4x-lite`, `tablet-galaxy-tab2`, `tablet-galaxy-tab2-color` |
| Tema día/noche (`applyThemeByHour` 7–20) | Sin cambios en este ciclo |
| Brillo Android / sensor de luz | Fuera de alcance |
| Commits / APKs | Solo si el usuario lo autoriza en la sesión |

## Arquitectura

### Overlay

- Elemento `#dim-overlay` en `index.html`:
  - `position: fixed; inset: 0; background: #000`
  - `pointer-events: none`
  - `opacity` controlada por JS (0 día … 0.30 noche)
  - `transition: opacity ~0.8s–1s linear` para suavizar entre ticks
  - `z-index` **por encima** del dashboard (`.screen`) y **por debajo** del city-picker (el selector no debe quedar velado ni bloquear toques)

### Curva temporal (modo real)

Sean `S` = sunset del día civil actual, `R` = sunrise del día civil correspondiente (tras medianoche: sunrise de “mañana” vía `forecast_days=2`).

| Intervalo | Opacidad overlay |
|-----------|------------------|
| Tras rampa de amanecer → hasta `S` | `0` |
| `S` → `S + 30 min` | Interpolación lineal `0 → 0.30` |
| `S + 30 min` → `R` | `0.30` |
| `R` → `R + 30 min` | Interpolación lineal `0.30 → 0` |

Sin `sunrise`/`sunset` disponibles (primer arranque sin cache, respuesta incompleta): opacidad `0`.

### Actualización

- El `tick()` existente (1 s) llama a `applyDimOverlay(now)` cuando no hay demo activa.
- Función pura recomendada: `dimOpacity(nowMs, sunsetMs, nextSunriseMs) → number`, donde `nextSunriseMs` es el amanecer **posterior** al `sunsetMs` en vigor (tras medianoche = sunrise de mañana). Open-Meteo devuelve ISO; se parsean a epoch ms una sola vez al recibir/cachear el clima.

## Componentes y datos

### Botón de test

- En `.top-right`, junto a 📍 y ↻.
- Icono `◐`, `title` / `aria-label`: “Probar atenuacion”.
- Al pulsar: modo **demo** ~12 s que comprime el ciclo:
  día (opacidad 0) → rampa atardecer → noche (0.30) → rampa amanecer → día (0).
- Durante la demo se ignora el reloj real; un segundo toque **cancela** y restaura el cálculo real.
- El botón **no** se deshabilita (para poder cancelar); usa flag `dimDemoActive` y estilo visual `busy` sin `disabled`. No lanzar una segunda demo en paralelo: si ya hay una, el toque solo cancela.

### Open-Meteo

- Añadir `sunrise,sunset` al parámetro `&daily=` de la URL de forecast (además de los campos ya pedidos).
- Persistir esos valores en el payload de `climadesk_last_weather` para atenuar offline con el último parte guardado.

### Java / brillo sistema

- Sin cambios en `MainActivity.java` ni permisos nuevos.

## Flujo

1. `loadWeather` / `restoreLastWeather` → extraen ISO de Open-Meteo, parsean a epoch y guardan `sunTimes` (`sunsetMs`, `nextSunriseMs`).
2. Cada `tick`: si no hay demo → `overlay.style.opacity = dimOpacity(...)`.
3. Botón test → reloj virtual acelerado actualiza la opacidad; al terminar (o cancelar) → modo real.

## Manejo de bordes

- Medianoche: sunset de hoy + sunrise de mañana (arrays `daily` de Open-Meteo).
- City-picker abierto: overlay debajo; no intercepta eventos.
- Variantes Color / Mono / Lite: misma lógica JS y markup del overlay/botón; adaptar solo si el layout `.top-right` difiere en alguna rama.

## Verificación

1. Pulsar `◐`: ciclo visual completo en ~12 s y vuelta al nivel real.
2. Tras un refresh con datos solares, comprobar que de día el overlay está a 0.
3. Offline con cache que incluya sunrise/sunset: la atenuación sigue aplicando.
4. Abrir selector de ciudad durante atenuación: panel legible y usable.
5. Compilar/instalar por variante solo si el usuario lo pide.

## Fuera de alcance

- `WindowManager` / `screenBrightness`
- Sensor de luz ambiental
- Unificar tema CSS día/noche con sunrise/sunset
- Modo solo-reloj
- GitHub Release / keystore
- PWA iOS
