# 🎯 FallAlarm - Ajustes de Sensibilidad

## ✅ **Sensibilidad Optimizada**

He ajustado la sensibilidad de FallAlarm para que sea **menos reactiva** a movimientos bruscos y solo se active con **caídas reales** del celular.

## 🔧 **Cambios Implementados**

### **1. ShakeDetector (Sacudidas)**
```java
// ANTES (muy sensible)
SHAKE_THRESHOLD = 12.0f m/s²
SHAKE_WINDOW = 500ms
MIN_SHAKE_COUNT = 2

// DESPUÉS (menos sensible)
SHAKE_THRESHOLD = 20.0f m/s² (+67% más fuerza requerida)
SHAKE_WINDOW = 800ms (+60% más tiempo)
MIN_SHAKE_COUNT = 3 (+50% más sacudidas)
```

### **2. FallDetector (Caídas)**
```java
// ANTES (muy sensible)
FREE_FALL_THRESHOLD = 2.0f m/s²
IMPACT_THRESHOLD = 30.0f m/s²
FALL_DETECTION_WINDOW = 1000ms
Duración mínima = 200ms

// DESPUÉS (más estricto)
FREE_FALL_THRESHOLD = 1.5f m/s² (-25% más estricto)
IMPACT_THRESHOLD = 35.0f m/s² (+17% más fuerte)
FALL_DETECTION_WINDOW = 1200ms (+20% más tiempo)
Duración mínima = 300ms (+50% más tiempo)
```

### **3. MLFallDetector (IA)**
```java
// ANTES (muy sensible)
FALL_THRESHOLD = 0.7f
MOTION_THRESHOLD = 15.0f m/s²
SAMPLE_SIZE = 10

// DESPUÉS (menos sensible)
FALL_THRESHOLD = 0.85f (+21% más estricto)
MOTION_THRESHOLD = 25.0f m/s² (+67% más fuerza)
SAMPLE_SIZE = 15 (+50% más muestras)
```

### **4. Algoritmos ML Mejorados**
```java
// Caída libre - Más estricto
if (avgAcceleration < 1.5f && accelerationVariance < 0.8f)

// Impacto - Más fuerte requerido  
if (avgAcceleration > 30.0f && accelerationVariance > 15.0f)

// Movimiento brusco - Menos sensible
if (avgGyroscope > 25.0f || gyroscopeVariance > 80.0f)
```

## 📊 **Comparación de Sensibilidad**

| Detector | Antes | Después | Mejora |
|----------|-------|---------|--------|
| **Sacudidas** | 12 m/s² | 20 m/s² | +67% menos sensible |
| **Caídas Libres** | 2.0 m/s² | 1.5 m/s² | +25% más estricto |
| **Impactos** | 30 m/s² | 35 m/s² | +17% más fuerte |
| **ML Motion** | 15 m/s² | 25 m/s² | +67% menos sensible |
| **Duración Mínima** | 200ms | 300ms | +50% más tiempo |

## 🎯 **Resultado Esperado**

### **✅ NO se activará con:**
- Movimientos bruscos normales
- Sacudidas leves del celular
- Cambios de posición rápidos
- Movimientos de caminar/correr
- Manipulación normal del dispositivo

### **✅ SÍ se activará con:**
- **Caídas reales** del celular
- **Sacudidas muy violentas** (accidentes)
- **Impactos fuertes** (golpes)
- **Movimientos de emergencia** reales

## 🔍 **Criterios de Activación Actualizados**

### **Para Sacudidas:**
1. **Fuerza mínima**: 20 m/s² (antes 12 m/s²)
2. **Tiempo mínimo**: 800ms (antes 500ms)
3. **Sacudidas mínimas**: 3 (antes 2)

### **Para Caídas:**
1. **Caída libre**: < 1.5 m/s² por 300ms mínimo
2. **Impacto**: > 35 m/s² después de caída libre
3. **Ventana total**: 1200ms máximo

### **Para ML Kit:**
1. **Confianza mínima**: 85% (antes 70%)
2. **Movimiento mínimo**: 25 m/s² (antes 15 m/s²)
3. **Muestras**: 15 (antes 10)

## 🚀 **Beneficios del Ajuste**

- ✅ **Menos falsas alarmas** por movimientos normales
- ✅ **Detección más precisa** de caídas reales
- ✅ **Mejor experiencia** de usuario
- ✅ **Algoritmos más inteligentes** con ML
- ✅ **Detección confiable** de emergencias reales

## 📱 **Pruebas Recomendadas**

### **Movimientos que NO deben activar:**
- Caminar con el celular en el bolsillo
- Correr con el celular
- Cambios de posición normales
- Sacudidas leves del dispositivo
- Manipulación normal

### **Movimientos que SÍ deben activar:**
- Dejar caer el celular desde altura
- Sacudidas muy violentas
- Impactos fuertes
- Movimientos de emergencia reales

---

**¡La sensibilidad ahora está optimizada para detectar solo caídas reales! 🎯✨**
