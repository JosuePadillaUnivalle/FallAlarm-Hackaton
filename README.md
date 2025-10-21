# FallAlarm - Detector de Caídas

Una aplicación Android desarrollada en Java que detecta caídas y sacudidas fuertes del teléfono, activando una alarma continua hasta que el usuario confirme que está bien.

## 🎯 Características

- **Detección de caídas**: Utiliza el acelerómetro para detectar caídas libres y impactos
- **Detección de sacudidas**: Identifica movimientos bruscos y violentos del dispositivo
- **Alarma continua**: Reproduce una alarma en bucle hasta que el usuario presione "Estoy bien"
- **Servicio en primer plano**: Mantiene la vigilancia activa en segundo plano
- **Reanudación automática**: Se reinicia automáticamente tras el reinicio del dispositivo
- **Sin GPS ni cámara**: Solo utiliza sensores del dispositivo

## 🔧 Requisitos

- Android 7.0 (API 24) o superior
- Sensores de acelerómetro y giroscopio
- Permisos de notificación (Android 13+)
- Permiso de vibración

## 📱 Funcionalidades

### Pantalla Principal
- Botón grande para activar/desactivar la vigilancia
- Estado visual del servicio (Activada/Desactivada)
- Diseño Material 3 con colores suaves
- Interfaz simple y elegante

### Detección de Eventos
- **Sacudida fuerte**: Magnitud > 12 m/s² en 500ms
- **Caída libre**: Aceleración < 2 m/s² seguida de impacto > 30 m/s²
- **Sensibilidad fija**: Sin configuración adicional

### Pantalla de Emergencia
- Pantalla fullscreen con fondo oscuro
- Alarma continua con vibración
- Botón "Estoy bien" para detener la alarma
- No se puede salir con el botón atrás

## 🏗️ Arquitectura

```
ui/
├── MainActivity.java          # Actividad principal
└── EmergencyActivity.java     # Pantalla de emergencia

sensors/
├── FallDetector.java         # Detector de caídas
└── ShakeDetector.java        # Detector de sacudidas

service/
└── FallAlarmService.java     # Servicio en primer plano

util/
├── SoundPlayer.java          # Reproductor de alarma
└── PermissionUtils.java      # Utilidades de permisos

broadcast/
└── BootReceiver.java         # Receptor de arranque
```

## 🔐 Permisos

- `FOREGROUND_SERVICE`: Para el servicio en primer plano
- `POST_NOTIFICATIONS`: Para notificaciones (Android 13+)
- `VIBRATE`: Para vibración de alarma
- `RECEIVE_BOOT_COMPLETED`: Para reanudación tras reinicio

## 🎨 Diseño

- **Material 3**: Interfaz moderna y consistente
- **Colores suaves**: Paleta de colores relajante
- **Tipografía clara**: Texto legible y bien estructurado
- **Botones grandes**: Fácil interacción
- **Animaciones sutiles**: Transiciones suaves

## 🚀 Instalación

1. Clona el repositorio
2. Abre el proyecto en Android Studio
3. Sincroniza las dependencias de Gradle
4. Compila e instala en tu dispositivo

## 📋 Uso

1. **Activar vigilancia**: Presiona el botón "Activar vigilancia"
2. **Detección automática**: La app detecta caídas y sacudidas
3. **Alarma de emergencia**: Se activa automáticamente
4. **Confirmar bienestar**: Presiona "Estoy bien" para detener

## ⚙️ Configuración Técnica

### Sensibilidad (valores fijos)
- **Sacudida**: 12 m/s²
- **Caída libre**: < 2 m/s²
- **Impacto**: > 30 m/s² (≈ 3g)

### Filtros
- **Filtro paso alto**: Elimina la gravedad
- **Promedio móvil**: Suaviza las lecturas
- **Ventana temporal**: 500ms para sacudidas, 1000ms para caídas

## 🔄 Flujo de Funcionamiento

1. Usuario activa la vigilancia
2. Servicio se inicia en primer plano
3. Sensores comienzan a monitorear
4. Al detectar evento → Pantalla de emergencia
5. Alarma continua hasta confirmación
6. Usuario presiona "Estoy bien"
7. Alarma se detiene y vuelve a vigilancia

## 🛠️ Desarrollo

### Compilación
```bash
./gradlew assembleDebug
```

### Estructura de paquetes
- `com.example.fallalarm.ui` - Interfaces de usuario
- `com.example.fallalarm.sensors` - Detectores de sensores
- `com.example.fallalarm.service` - Servicios en segundo plano
- `com.example.fallalarm.util` - Utilidades
- `com.example.fallalarm.broadcast` - Receptores de broadcast

## 📄 Licencia

Este proyecto está desarrollado como ejemplo educativo.

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor, crea un issue o pull request.

---

**FallAlarm** - Tu seguridad, nuestra prioridad 🛡️
