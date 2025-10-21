package com.example.fallalarm.ml;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Analizador de patrones de movimiento usando técnicas de ML
 * Clasifica diferentes tipos de movimiento para mejorar la detección de caídas
 */
public class MotionPatternAnalyzer {
    
    private static final String TAG = "MotionPatternAnalyzer";
    private static final int PATTERN_SIZE = 20;
    private static final float FALL_PATTERN_THRESHOLD = 0.75f;
    
    private List<MotionData> motionHistory = new ArrayList<>();
    private MotionPatternClassifier classifier;
    
    public MotionPatternAnalyzer() {
        this.classifier = new MotionPatternClassifier();
    }
    
    /**
     * Agrega nuevos datos de movimiento para análisis
     */
    public void addMotionData(float[] acceleration, float[] gyroscope, long timestamp) {
        MotionData data = new MotionData(acceleration, gyroscope, timestamp);
        motionHistory.add(data);
        
        // Mantener solo los últimos datos
        if (motionHistory.size() > PATTERN_SIZE) {
            motionHistory.remove(0);
        }
        
        // Analizar patrón si tenemos suficientes datos
        if (motionHistory.size() >= PATTERN_SIZE) {
            analyzeMotionPattern();
        }
    }
    
    /**
     * Analiza el patrón de movimiento actual
     */
    private void analyzeMotionPattern() {
        if (motionHistory.size() < PATTERN_SIZE) {
            return;
        }
        
        // Extraer características del patrón
        MotionFeatures features = extractFeatures();
        
        // Clasificar el patrón
        MotionClassification classification = classifier.classify(features);
        
        Log.d(TAG, "Patrón clasificado como: " + classification.getType() + 
              " (confianza: " + classification.getConfidence() + ")");
        
        // Si es un patrón de caída, notificar
        if (classification.getType().equals("fall") && 
            classification.getConfidence() > FALL_PATTERN_THRESHOLD) {
            onFallPatternDetected(classification.getConfidence());
        }
    }
    
    /**
     * Extrae características del patrón de movimiento
     */
    private MotionFeatures extractFeatures() {
        MotionFeatures features = new MotionFeatures();
        
        // Características de aceleración
        features.avgAcceleration = calculateAverageAcceleration();
        features.maxAcceleration = calculateMaxAcceleration();
        features.accelerationVariance = calculateAccelerationVariance();
        features.accelerationJerk = calculateAccelerationJerk();
        
        // Características de giroscopio
        features.avgGyroscope = calculateAverageGyroscope();
        features.maxGyroscope = calculateMaxGyroscope();
        features.gyroscopeVariance = calculateGyroscopeVariance();
        
        // Características temporales
        features.duration = calculateDuration();
        features.frequency = calculateFrequency();
        
        // Características de dirección
        features.directionChange = calculateDirectionChange();
        features.rotationIntensity = calculateRotationIntensity();
        
        return features;
    }
    
    private float calculateAverageAcceleration() {
        float sum = 0.0f;
        for (MotionData data : motionHistory) {
            sum += data.getAccelerationMagnitude();
        }
        return sum / motionHistory.size();
    }
    
    private float calculateMaxAcceleration() {
        float max = 0.0f;
        for (MotionData data : motionHistory) {
            max = Math.max(max, data.getAccelerationMagnitude());
        }
        return max;
    }
    
    private float calculateAccelerationVariance() {
        float avg = calculateAverageAcceleration();
        float sum = 0.0f;
        for (MotionData data : motionHistory) {
            float diff = data.getAccelerationMagnitude() - avg;
            sum += diff * diff;
        }
        return sum / motionHistory.size();
    }
    
    private float calculateAccelerationJerk() {
        if (motionHistory.size() < 2) return 0.0f;
        
        float jerkSum = 0.0f;
        for (int i = 1; i < motionHistory.size(); i++) {
            float jerk = Math.abs(motionHistory.get(i).getAccelerationMagnitude() - 
                                 motionHistory.get(i-1).getAccelerationMagnitude());
            jerkSum += jerk;
        }
        return jerkSum / (motionHistory.size() - 1);
    }
    
    private float calculateAverageGyroscope() {
        float sum = 0.0f;
        for (MotionData data : motionHistory) {
            sum += data.getGyroscopeMagnitude();
        }
        return sum / motionHistory.size();
    }
    
    private float calculateMaxGyroscope() {
        float max = 0.0f;
        for (MotionData data : motionHistory) {
            max = Math.max(max, data.getGyroscopeMagnitude());
        }
        return max;
    }
    
    private float calculateGyroscopeVariance() {
        float avg = calculateAverageGyroscope();
        float sum = 0.0f;
        for (MotionData data : motionHistory) {
            float diff = data.getGyroscopeMagnitude() - avg;
            sum += diff * diff;
        }
        return sum / motionHistory.size();
    }
    
    private float calculateDuration() {
        if (motionHistory.size() < 2) return 0.0f;
        return (motionHistory.get(motionHistory.size() - 1).timestamp - 
                motionHistory.get(0).timestamp) / 1000.0f; // en segundos
    }
    
    private float calculateFrequency() {
        return motionHistory.size() / calculateDuration();
    }
    
    private float calculateDirectionChange() {
        if (motionHistory.size() < 3) return 0.0f;
        
        int directionChanges = 0;
        for (int i = 2; i < motionHistory.size(); i++) {
            float acc1 = motionHistory.get(i-2).getAccelerationMagnitude();
            float acc2 = motionHistory.get(i-1).getAccelerationMagnitude();
            float acc3 = motionHistory.get(i).getAccelerationMagnitude();
            
            if ((acc2 > acc1 && acc2 > acc3) || (acc2 < acc1 && acc2 < acc3)) {
                directionChanges++;
            }
        }
        return (float) directionChanges / (motionHistory.size() - 2);
    }
    
    private float calculateRotationIntensity() {
        float sum = 0.0f;
        for (MotionData data : motionHistory) {
            sum += data.getGyroscopeMagnitude();
        }
        return sum / motionHistory.size();
    }
    
    private void onFallPatternDetected(float confidence) {
        Log.w(TAG, "Patrón de caída detectado con confianza: " + confidence);
        // Aquí se podría notificar al servicio principal
    }
    
    /**
     * Clase para datos de movimiento
     */
    private static class MotionData {
        public float[] acceleration;
        public float[] gyroscope;
        public long timestamp;
        
        public MotionData(float[] acceleration, float[] gyroscope, long timestamp) {
            this.acceleration = acceleration.clone();
            this.gyroscope = gyroscope.clone();
            this.timestamp = timestamp;
        }
        
        public float getAccelerationMagnitude() {
            return (float) Math.sqrt(
                acceleration[0] * acceleration[0] + 
                acceleration[1] * acceleration[1] + 
                acceleration[2] * acceleration[2]
            );
        }
        
        public float getGyroscopeMagnitude() {
            return (float) Math.sqrt(
                gyroscope[0] * gyroscope[0] + 
                gyroscope[1] * gyroscope[1] + 
                gyroscope[2] * gyroscope[2]
            );
        }
    }
    
    /**
     * Clase para características de movimiento
     */
    private static class MotionFeatures {
        public float avgAcceleration;
        public float maxAcceleration;
        public float accelerationVariance;
        public float accelerationJerk;
        public float avgGyroscope;
        public float maxGyroscope;
        public float gyroscopeVariance;
        public float duration;
        public float frequency;
        public float directionChange;
        public float rotationIntensity;
    }
    
    /**
     * Clasificador de patrones de movimiento
     */
    private static class MotionPatternClassifier {
        
        public MotionClassification classify(MotionFeatures features) {
            float fallScore = calculateFallScore(features);
            float shakeScore = calculateShakeScore(features);
            float normalScore = calculateNormalScore(features);
            
            if (fallScore > shakeScore && fallScore > normalScore) {
                return new MotionClassification("fall", fallScore);
            } else if (shakeScore > normalScore) {
                return new MotionClassification("shake", shakeScore);
            } else {
                return new MotionClassification("normal", normalScore);
            }
        }
        
        private float calculateFallScore(MotionFeatures features) {
            float score = 0.0f;
            
            // Caída libre: baja aceleración
            if (features.avgAcceleration < 3.0f) {
                score += 0.3f;
            }
            
            // Impacto: alta aceleración
            if (features.maxAcceleration > 20.0f) {
                score += 0.4f;
            }
            
            // Variabilidad alta
            if (features.accelerationVariance > 5.0f) {
                score += 0.2f;
            }
            
            // Duración corta
            if (features.duration < 2.0f) {
                score += 0.1f;
            }
            
            return Math.min(score, 1.0f);
        }
        
        private float calculateShakeScore(MotionFeatures features) {
            float score = 0.0f;
            
            // Alta frecuencia
            if (features.frequency > 10.0f) {
                score += 0.3f;
            }
            
            // Muchos cambios de dirección
            if (features.directionChange > 0.3f) {
                score += 0.3f;
            }
            
            // Alta rotación
            if (features.rotationIntensity > 5.0f) {
                score += 0.4f;
            }
            
            return Math.min(score, 1.0f);
        }
        
        private float calculateNormalScore(MotionFeatures features) {
            float score = 0.0f;
            
            // Aceleración normal
            if (features.avgAcceleration >= 8.0f && features.avgAcceleration <= 12.0f) {
                score += 0.4f;
            }
            
            // Baja variabilidad
            if (features.accelerationVariance < 2.0f) {
                score += 0.3f;
            }
            
            // Baja rotación
            if (features.rotationIntensity < 2.0f) {
                score += 0.3f;
            }
            
            return Math.min(score, 1.0f);
        }
    }
    
    /**
     * Clasificación de movimiento
     */
    private static class MotionClassification {
        private String type;
        private float confidence;
        
        public MotionClassification(String type, float confidence) {
            this.type = type;
            this.confidence = confidence;
        }
        
        public String getType() { return type; }
        public float getConfidence() { return confidence; }
    }
}
