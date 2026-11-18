# APKs Compiladas de ClimaDesk

Este directorio centraliza las versiones compiladas de ClimaDesk:

| Archivo | Dispositivo Objetivo | Características |
|---|---|---|
| `Climadesk-Color-v1.8.2.apk` | Samsung Galaxy Tab 2 | E-Ink color (fondo blanco roto, sol, lluvia, temperatura a color, 7 días de previsión) |
| `Climadesk-v1.8.2.apk` | Samsung Galaxy Tab 2 | E-Ink monocromo original (7 días de previsión, calidad de aire) |
| `Climadesk-Lite-v1.8.3.apk` | Xiaomi Redmi 4X | Edición Lite ultra-ligera (640x360 dp; AMANECER/ATARDECER en columna) |

Las versiones anteriores (v1.8, v1.8.1, Lite v1.8.2) se conservan en la carpeta como respaldo.

> **Nota:** Los archivos binarios `.apk` están excluidos del historial de Git según `.gitignore`. Puedes compilarlos con `gradlew.bat assembleDebug` situándote en la rama correspondiente.
