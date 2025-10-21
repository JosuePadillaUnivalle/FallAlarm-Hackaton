package com.example.fallalarm.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Detector de caídas basado en acelerómetro
 * Detecta caídas libres y impactos
 */
public class FallDetector implements SensorEventListener {
    
    private static final float FREE_FALL_THRESHOLD = 1.0f; // m/s² - Muy estricto para caída libre
    private static final float IMPACT_THRESHOLD = 45.0f; // m/s² (≈ 4.5g) - Impacto muy fuerte requerido
    private static final long FALL_DETECTION_WINDOW = 1500; // ms - Ventana mucho más larga
    
    private FallDetectionListener listener;
    private boolean isInFreeFall = false;
    private long freeFallStartTime = 0;
    private float[] gravity = new float[3];
    private float[] linearAcceleration = new float[3];
    
    public interface FallDetectionListener {
        void onFallDetected();
    }
    
    public FallDetector(FallDetectionListener listener) {
        this.listener = listener;
    }
    
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            processAccelerometerData(event.values);
        }
    }
    
    private void processAccelerometerData(float[] values) {
        // Filtro de paso alto para eliminar la gravedad
        final float alpha = 0.8f;
        
        // Aislar la fuerza de la gravedad con el filtro paso bajo
        gravity[0] = alpha * gravity[0] + (1 - alpha) * values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * values[2];
        
        // Remover la contribución de la gravedad
        linearAcceleration[0] = values[0] - gravity[0];
        linearAcceleration[1] = values[1] - gravity[1];
        linearAcceleration[2] = values[2] - gravity[2];
        
        // Calcular la magnitud de la aceleración lineal
        float magnitude = (float) Math.sqrt(
            linearAcceleration[0] * linearAcceleration[0] +
            linearAcceleration[1] * linearAcceleration[1] +
            linearAcceleration[2] * linearAcceleration[2]
        );
        
        // Detectar caída libre
        if (magnitude < FREE_FALL_THRESHOLD) {
            if (!isInFreeFall) {
                isInFreeFall = true;
                freeFallStartTime = System.currentTimeMillis();
            }
        } else {
            // Detectar impacto después de caída libre
            if (isInFreeFall && magnitude > IMPACT_THRESHOLD) {
                long fallDuration = System.currentTimeMillis() - freeFallStartTime;
                
                // Verificar que la caída libre duró al menos 500ms - Mucho más tiempo requerido
                if (fallDuration >= 500 && fallDuration <= FALL_DETECTION_WINDOW) {
                    if (listener != null) {
                        listener.onFallDetected();
                    }
                }
            }
            isInFreeFall = false;
        }
    }
    
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No se requiere acción específica
    }
    
    public void reset() {
        isInFreeFall = false;
        freeFallStartTime = 0;
    }
}
