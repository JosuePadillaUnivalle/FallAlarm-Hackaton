# 📱 Instrucciones para Ejecutar FallAlarm

## ✅ **Estado de la Aplicación**
La aplicación FallAlarm ha sido compilada exitosamente y está lista para ejecutarse.

## 🚀 **Pasos para Ejecutar**

### 1. **Instalar en Dispositivo**
```bash
# Conectar dispositivo Android o usar emulador
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 2. **Permisos Necesarios**
La aplicación solicitará automáticamente los siguientes permisos:
- ✅ **Notificaciones** (Android 13+)
- ✅ **Vibración**
- ✅ **Servicio en primer plano**

### 3. **Primer Uso**
1. **Abrir la aplicación** FallAlarm
2. **Presionar "Activar vigilancia"**
3. **Conceder permisos** cuando se soliciten
4. **Verificar notificación** en la barra de estado

## 🔧 **Solución de Problemas Comunes**

### ❌ **Error: "No se puede activar vigilancia"**
**Solución:**
- Verificar que el dispositivo tenga sensores de acelerómetro
- Reiniciar la aplicación
- Verificar permisos en Configuración > Aplicaciones > FallAlarm

### ❌ **Error: "Alarma no suena"**
**Solución:**
- Verificar que el volumen esté activado
- Verificar que no esté en modo silencioso
- Probar con otro dispositivo

### ❌ **Error: "Servicio se detiene"**
**Solución:**
- Verificar que la batería no esté optimizada para la app
- Desactivar optimización de batería para FallAlarm
- Verificar que el servicio tenga permisos de primer plano

## 🧪 **Pruebas de Funcionamiento**

### **Prueba 1: Detección de Sacudida**
1. Activar vigilancia
2. Agitar el dispositivo fuertemente
3. Debería aparecer pantalla de emergencia
4. Presionar "Estoy bien" para detener

### **Prueba 2: Detección de Caída**
1. Activar vigilancia
2. Simular caída (mover dispositivo hacia abajo rápidamente)
3. Debería detectar la caída libre + impacto
4. Presionar "Estoy bien" para detener

### **Prueba 3: Servicio en Segundo Plano**
1. Activar vigilancia
2. Minimizar la aplicación
3. Verificar que la notificación esté activa
4. Probar detección con app minimizada

## 📊 **Logs de Depuración**

Para ver los logs de la aplicación:
```bash
adb logcat | grep "FallAlarm"
```

**Logs importantes:**
- `FallAlarmService: Servicio iniciado`
- `FallAlarmService: Monitoreo de sensores iniciado`
- `FallAlarmService: Caída detectada!`
- `FallAlarmService: Sacudida detectada!`

## ⚙️ **Configuración Avanzada**

### **Sensibilidad (Valores Fijos)**
- **Sacudida**: 12 m/s²
- **Caída libre**: < 2 m/s²
- **Impacto**: > 30 m/s²

### **Optimización de Batería**
Para mejor rendimiento:
1. Ir a Configuración > Batería > Optimización de batería
2. Buscar "FallAlarm"
3. Seleccionar "No optimizar"

## 🔄 **Reinicio Automático**

La aplicación se reinicia automáticamente después de:
- Reinicio del dispositivo
- Actualización del sistema
- Limpieza de memoria del sistema

## 📱 **Compatibilidad**

- **Android**: 7.0 (API 24) o superior
- **Sensores**: Acelerómetro requerido
- **Memoria**: Mínimo 50MB RAM
- **Almacenamiento**: 10MB espacio libre

## 🆘 **Soporte**

Si encuentras problemas:
1. Verificar logs con `adb logcat`
2. Reiniciar la aplicación
3. Verificar permisos
4. Probar en otro dispositivo

---

**¡La aplicación FallAlarm está lista para protegerte! 🛡️**
