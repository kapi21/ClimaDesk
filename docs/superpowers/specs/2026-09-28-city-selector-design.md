# Design: Selector de ciudad con búsqueda (P1.1)

Fecha: 2026-09-28  
Proyecto: ClimaDesk  
Estado: aprobado en conversación; pendiente de plan de implementación

## Objetivo

Sustituir `window.prompt` al tocar el nombre de la ciudad por un panel a pantalla completa con sugerencias en vivo vía Open-Meteo Geocoding, de forma que el usuario elija inequívocamente entre resultados.

## Decisiones

| Tema | Elección |
|------|----------|
| Interacción | Sugerencias en vivo al tipear |
| Etiqueta de resultado | `nombre, admin1, país` (omitir admin1 si vacío) |
| Contenedor UI | Panel a pantalla completa |
| Acciones en panel | Buscar, elegir, cancelar (sin GPS ni borrar aquí) |
| Implementación | Solo en `index.html` (WebView); sin cambios Java |
| APK | No empaquetar salvo petición explícita o necesidad estricta |

## Fuera de alcance

- Renombrar `applicationId` / package (P1.2)
- Icono launcher, modo solo-reloj, kiosk duro, release firmada
- Historial de ciudades recientes
- Botón de ubicación automática dentro del panel (sigue existiendo 📍 en la barra)

## UI y flujo

1. El usuario toca `#city`.
2. Se abre un overlay a pantalla completa alineado con el tema día/noche del kiosk.
3. Cabecera: título «Elegir ciudad» + botón Cancelar (cierra sin cambiar ubicación ni storage).
4. Input con foco automático; placeholder «Buscar ciudad…»; valor inicial = ciudad manual guardada si existe. Si al abrir el valor tiene ≥ 2 caracteres, se lanza una búsqueda inicial (misma API).
5. Debounce ≈ 300 ms. Con menos de 2 caracteres no se busca; se muestra un estado vacío («Escribe para buscar»).
6. Con ≥ 2 caracteres: petición a Open-Meteo `v1/search` (`language=es`, `count=8`).
7. Lista de filas con la etiqueta completa. Al tocar una:
   - se persiste la elección;
   - se cierra el panel;
   - `#city` muestra el **nombre corto** (`name`), no el label completo;
   - se actualiza `currentLocation` y se refresca el clima.
8. Estados del panel: vacío / buscando / sin resultados / error de red («No se pudo buscar»).
9. El botón 📍 no cambia de comportamiento: fuerza GPS y limpia la ciudad manual.

## Datos y código

Archivo único a tocar: `kiosk-app/app/src/main/assets/index.html`.

### API

- Base existente: `https://geocoding-api.open-meteo.com/v1/search`
- Nueva función `searchCities(query)` → lista de objetos:
  - `lat`, `lon`, `name`, `admin1`, `country`, `label`
- `label` = unir con comas las partes no vacías de `name`, `admin1`, `country`.
- Al elegir un resultado, no se re-busca por nombre a ciegas: se usan las coords y el `name` del ítem elegido.
- `geocodeCity` (arranque / fallback) puede seguir resolviendo por nombre si solo hay texto guardado; si hay lat/lon guardados, se prefieren.

### Persistencia (`localStorage`)

| Clave | Uso |
|-------|-----|
| `climadesk_manual_city` | Texto / nombre (como hoy) |
| `climadesk_manual_lat` | Latitud de la ciudad elegida (nuevo) |
| `climadesk_manual_lon` | Longitud de la ciudad elegida (nuevo) |

Al forzar ubicación automática (📍), se eliminan las tres claves (o al menos ciudad + coords).

### Concurrencia

Si el usuario escribe rápido, solo se aplica la respuesta de la búsqueda más reciente (contador de secuencia). No depender de `AbortController` (compatibilidad WebView antiguo).

## Errores

| Situación | Comportamiento |
|-----------|----------------|
| Red / HTTP fallido en búsqueda | Mensaje en el panel; ubicación actual intacta |
| Sin resultados | «Sin resultados» |
| Fallo al cargar clima tras elegir | Misma zona `#err` que hoy; ciudad ya persistida |

## Verificación

Sin APK release. Preferir probar el HTML en navegador de escritorio o, si hace falta instalar debug en dispositivo, solo cuando el usuario lo pida o sea estrictamente necesario para validar el WebView.

Checklist:

1. Abrir selector desde el nombre de ciudad.
2. Escribir un prefijo ambiguo (p. ej. «Madr») y ver varias filas con región/país.
3. Elegir una → panel cierra, clima actualiza, recarga de página conserva la ciudad.
4. Cancelar a mitad → no cambia ubicación ni storage.
5. 📍 fuerza GPS y limpia ciudad manual.

## Criterios de éxito

- Ya no se usa `window.prompt` para elegir ciudad.
- El usuario puede distinguir homónimos gracias a admin1/país.
- La elección persiste entre sesiones (nombre + coords).
- El flujo GPS existente sigue funcionando.
