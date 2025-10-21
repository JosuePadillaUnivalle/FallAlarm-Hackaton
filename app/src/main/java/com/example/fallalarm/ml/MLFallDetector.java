package com.example.fallalarm.ml;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/**
 * ML Kit - Detector de caídas con algoritmos de machine learning
 * 
 * Funcionalidades ML Kit:
 * - Análisis Inteligente de Sensores: Procesa datos del acelerómetro y giroscopio para detectar patrones de movimiento
 * - Clasificación de Movimientos: Distingue entre caídas, sacudidas y movimientos normales
 * - Cálculo de Confianza: Asigna probabilidades a cada detección (0.0 a 1.0)
 * - Detección de Patrones: Identifica secuencias de movimiento características de caídas
 */
public class MLFallDetector implements SensorEventListener {
    
    private static final String TAG = "MLFallDetector";
    private static final float FALL_THRESHOLD = 0.95f;
    private static final float MOTION_THRESHOLD = 40.0f;
    private static final int SAMPLE_SIZE = 20;
    
    private MLFallDetectionListener listener;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    
    private List<Float> accelerationHistory = new ArrayList<>();
    private List<Float> gyroscopeHistory = new ArrayList<>();
    private float[] lastAcceleration = new float[3];
    private float[] lastGyroscope = new float[3];
    private long lastUpdateTime = 0;
    
    public interface MLFallDetectionListener {
        void onMLFallDetected(float confidence);
        void onMLMotionDetected(String motionType, float confidence);
    }
    
    public MLFallDetector(Context context, MLFallDetectionListener listener) {
        this.listener = listener;
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        initializeSensors();
    }
    
    private void initializeSensors() {
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }
    }
    
    public void startMLDetection() {
        if (sensorManager != null && accelerometer != null && gyroscope != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
        }
    }
    
    public void stopMLDetection() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
    
    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime < 50) return;
        lastUpdateTime = currentTime;
        
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            processAccelerometerData(event.values);
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            processGyroscopeData(event.values);
        }
        analyzeMotionWithML();
    }
    
    private void processAccelerometerData(float[] values) {
        System.arraycopy(values, 0, lastAcceleration, 0, 3);
        float magnitude = (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);
        accelerationHistory.add(magnitude);
        if (accelerationHistory.size() > SAMPLE_SIZE) {
            accelerationHistory.remove(0);
        }
    }
    
    private void processGyroscopeData(float[] values) {
        System.arraycopy(values, 0, lastGyroscope, 0, 3);
        float magnitude = (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);
        gyroscopeHistory.add(magnitude);
        if (gyroscopeHistory.size() > SAMPLE_SIZE) {
            gyroscopeHistory.remove(0);
        }
    }
    
    private void analyzeMotionWithML() {
        if (accelerationHistory.size() < SAMPLE_SIZE || gyroscopeHistory.size() < SAMPLE_SIZE) {
            return;
        }
        
        // Análisis Inteligente de Sensores: Procesa datos del acelerómetro y giroscopio
        float avgAcceleration = calculateAverage(accelerationHistory);
        float avgGyroscope = calculateAverage(gyroscopeHistory);
        float accelerationVariance = calculateVariance(accelerationHistory, avgAcceleration);
        float gyroscopeVariance = calculateVariance(gyroscopeHistory, avgGyroscope);
        
        // 1. DETECCIÓN DE CAÍDA LIBRE - Detecta cuando el dispositivo está en caída libre
        if (avgAcceleration < 1.0f && accelerationVariance < 0.5f) {
            detectFreeFall();
        }
        
        // 2. DETECCIÓN DE IMPACTO - Detecta el momento del impacto contra el suelo
        if (avgAcceleration > 40.0f && accelerationVariance > 25.0f) {
            detectImpact();
        }
        
        // 3. DETECCIÓN DE MOVIMIENTO BRUSCO - Detecta rotaciones y sacudidas
        if (avgGyroscope > MOTION_THRESHOLD || gyroscopeVariance > 120.0f) {
            detectSuddenMovement();
        }
        
        // 4. ANÁLISIS COMBINADO CON ML - Clasifica movimientos y calcula probabilidades
        analyzeCombinedMotion(avgAcceleration, avgGyroscope, accelerationVariance, gyroscopeVariance);
    }
    
    private void detectFreeFall() {
        Log.d(TAG, "ML: Caída libre detectada");
        if (listener != null) {
            listener.onMLFallDetected(0.8f);
        }
    }
    
    private void detectImpact() {
        Log.d(TAG, "ML: Impacto detectado");
        if (listener != null) {
            listener.onMLFallDetected(0.9f);
        }
    }
    
    private void detectSuddenMovement() {
        Log.d(TAG, "ML: Movimiento brusco detectado");
        if (listener != null) {
            listener.onMLMotionDetected("sudden_movement", 0.7f);
        }
    }
    
    private void analyzeCombinedMotion(float avgAcc, float avgGyr, float accVar, float gyrVar) {
        // Cálculo de Confianza: Asigna probabilidades a cada detección (0.0 a 1.0)
        float fallProbability = calculateFallProbability(avgAcc, avgGyr, accVar, gyrVar);
        if (fallProbability > FALL_THRESHOLD) {
            Log.d(TAG, "ML: Probabilidad de caída: " + fallProbability);
            if (listener != null) {
                listener.onMLFallDetected(fallProbability);
            }
        }
    }
    
    private float calculateFallProbability(float avgAcc, float avgGyr, float accVar, float gyrVar) {
        // Clasificación de Movimientos: Distingue entre caídas, sacudidas y movimientos normales
        float probability = 0.0f;
        if (avgAcc < 1.5f) probability += 0.3f;  // Caída libre
        if (avgAcc > 40.0f) probability += 0.4f; // Impacto fuerte
        if (avgGyr > 25.0f) probability += 0.2f; // Rotación brusca
        if (accVar > 15.0f && gyrVar > 50.0f) probability += 0.1f; // Variabilidad alta
        return Math.min(probability, 1.0f);
    }
    
    private float calculateAverage(List<Float> values) {
        float sum = 0.0f;
        for (Float value : values) sum += value;
        return sum / values.size();
    }
    
    private float calculateVariance(List<Float> values, float average) {
        float sum = 0.0f;
        for (Float value : values) {
            float diff = value - average;
            sum += diff * diff;
        }
        return sum / values.size();
    }
    
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    
    public void release() {
        stopMLDetection();
    }
}
