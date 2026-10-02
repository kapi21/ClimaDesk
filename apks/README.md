# APKs Compiladas de ClimaDesk

Este directorio centraliza las versiones compiladas de ClimaDesk:

| Archivo | Dispositivo Objetivo | Características |
|---|---|---|
| `app-tablet-color-debug.apk` | Samsung Galaxy Tab 2 | E-Ink color (fondo blanco roto, sol, lluvia, temperatura a color, 7 días de previsión) |
| `app-tablet-debug.apk` | Samsung Galaxy Tab 2 | E-Ink monocromo original (7 días de previsión, calidad de aire) |
| `app-redmi-debug.apk` | Xiaomi Redmi 4X | Edición Lite ultra-ligera (640x360 dp adaptado a 720p sin desbordes) |

> **Nota:** Los archivos binarios `.apk` están excluidos del historial de Git según `.gitignore`. Puedes compilarlos con `gradlew.bat assembleDebug` situándote en la rama correspondiente.
