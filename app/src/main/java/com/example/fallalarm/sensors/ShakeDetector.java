package com.example.fallalarm.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Detector de sacudidas basado en acelerómetro
 * Detecta movimientos bruscos y violentos del dispositivo
 */
public class ShakeDetector implements SensorEventListener {
    
    private static final float SHAKE_THRESHOLD = 35.0f; // m/s² - Mucho menos sensible
    private static final long SHAKE_WINDOW = 1200; // ms - Ventana mucho más larga
    private static final int MIN_SHAKE_COUNT = 4; // Muchas más sacudidas requeridas
    
    private ShakeDetectionListener listener;
    private long lastShakeTime = 0;
    private int shakeCount = 0;
    private float[] gravity = new float[3];
    private float[] linearAcceleration = new float[3];
    
    public interface ShakeDetectionListener {
        void onShakeDetected();
    }
    
    public ShakeDetector(ShakeDetectionListener listener) {
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
        
        long currentTime = System.currentTimeMillis();
        
        // Detectar sacudida fuerte
        if (magnitude > SHAKE_THRESHOLD) {
            if (currentTime - lastShakeTime < SHAKE_WINDOW) {
                shakeCount++;
            } else {
                shakeCount = 1;
            }
            
            lastShakeTime = currentTime;
            
            // Si hay suficientes sacudidas en el tiempo de ventana
            if (shakeCount >= MIN_SHAKE_COUNT) {
                if (listener != null) {
                    listener.onShakeDetected();
                }
                shakeCount = 0; // Reset para evitar múltiples detecciones
            }
        } else {
            // Reset del contador si no hay movimiento
            if (currentTime - lastShakeTime > SHAKE_WINDOW) {
                shakeCount = 0;
            }
        }
    }
    
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No se requiere acción específica
    }
    
    public void reset() {
        shakeCount = 0;
        lastShakeTime = 0;
    }
}
