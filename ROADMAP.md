# ROADMAP — ClimaDesk

Hoja de ruta del proyecto **ClimaDesk**, organizada por dispositivos, funcionalidades y estado de implementación.

---

## 📱 Dispositivos y Plataformas

| Plataforma / Dispositivo | Estado | Detalles técnicos |
|---|---|---|
| **Samsung Galaxy Tab 2** (Android 6.0) | ✅ Completado (v1.8) | APK dedicado Monocromo y Color E-Ink (`c32063e15928a6f`) |
| **Xiaomi Redmi 4X** (Android 7.1.2) | ✅ Completado (v1.8) | APK dedicado Lite 640x360 dp (`6f207ef7d440`) |
| **Apple iPhone XS** (iOS 17+) | 📋 En backlog / Planificado | PWA a pantalla completa vía Safari + Acceso Guiado |
| **Raspberry Pi 3B / Pi 400** | 💡 Propuesta futura | Modo Kiosk en Chromium sobre Raspberry Pi OS |

---

## 🎯 Tareas y Funcionalidades por Prioridad

### Prioridad 1 (P1) — Alta / Próxima Sesión
- [ ] **PWA / iOS Web Ready (iPhone XS)**:
  - Añadir soporte `apple-mobile-web-app-capable` y meta tags de viewport iOS.
  - Gestión de márgenes para el Notch con `env(safe-area-inset-*)`.
  - **Modo OLED Puro (`#000000`)**: apagado real de píxeles para mínimo consumo.
  - **Protección anti-burn-in**: micro-desplazamiento sutil periódico de píxeles (*pixel-shift*) para preservar la pantalla OLED.
- [ ] **Despliegue Web Estático**:
  - Habilitar GitHub Pages o contenedor LXC local para servir la versión web/PWA en red local o pública.

### Prioridad 2 (P2) — Media
- [ ] **Modo Solo-Reloj**:
  - Opción en menú de ajustes para ocultar paneles meteorológicos y maximizar el reloj.
- [ ] **Ajuste fino de brillo / Sensor de luz**:
  - Atenuación automática nocturna para no molestar en dormitorios o mesas de noche.

### Prioridad 3 (P3) — Mantenimiento e Infraestructura
- [ ] **Keystore de producción Android**:
  - Creación de `.jks` para firmar APKs de release oficiales sin depender de claves de debug.
- [ ] **Sincronización LXC**:
  - Si se añade backend o servidor estático local, integrar con `deploy_lxc_patch.py`.

---

## 📦 Historial de Versiones

- **v1.8** (2026-10-02): Lanzamiento oficial multi-dispositivo con 3 binarios en GitHub Releases (Galaxy Tab 2 Color, Tab 2 Mono y Redmi 4X Lite).
- **v1.0 - v1.7**: Prototipos iterativos de kiosk Android, geolocalización Open-Meteo y diseño E-Ink.
