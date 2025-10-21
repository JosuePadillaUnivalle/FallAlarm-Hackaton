package com.example.fallalarm.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.fallalarm.R;
import com.example.fallalarm.broadcast.BootReceiver;
import com.example.fallalarm.ml.MLFallDetector;
import com.example.fallalarm.ml.MotionPatternAnalyzer;
import com.example.fallalarm.sensors.FallDetector;
import com.example.fallalarm.sensors.ShakeDetector;
import com.example.fallalarm.ui.EmergencyActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FallAlarmService extends Service implements 
    FallDetector.FallDetectionListener, ShakeDetector.ShakeDetectionListener,
    MLFallDetector.MLFallDetectionListener {
    
    private static final String TAG = "FallAlarmService";
    private static final String CHANNEL_ID = "fall_alarm_service";
    private static final int NOTIFICATION_ID = 1;
    
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private FallDetector fallDetector;
    private ShakeDetector shakeDetector;
    private MLFallDetector mlFallDetector;
    private MotionPatternAnalyzer motionAnalyzer;
    private ExecutorService sensorExecutor;
    private Vibrator vibrator;
    
    private boolean isServiceRunning = false;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Servicio creado");
        
        initializeSensors();
        initializeMLKit();
        createNotificationChannel();
        sensorExecutor = Executors.newSingleThreadExecutor();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Servicio iniciado");
        
        if (!isServiceRunning) {
            startForegroundService();
            startSensorMonitoring();
            isServiceRunning = true;
            
            // Marcar servicio como activo para reanudación tras reinicio
            BootReceiver.setServiceActive(this, true);
        }
        
        return START_STICKY; // Reiniciar automáticamente si se mata el servicio
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Servicio destruido");
        
        stopSensorMonitoring();
        if (sensorExecutor != null) {
            sensorExecutor.shutdown();
        }
        isServiceRunning = false;
        
        // Marcar servicio como inactivo
        BootReceiver.setServiceActive(this, false);
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Servicio no vinculado
    }
    
    private void initializeSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            
            if (accelerometer != null) {
                fallDetector = new FallDetector(this);
                shakeDetector = new ShakeDetector(this);
                Log.d(TAG, "Sensores inicializados correctamente");
            } else {
                Log.e(TAG, "Acelerómetro no disponible");
            }
        } else {
            Log.e(TAG, "SensorManager no disponible");
        }
    }
    
    private void initializeMLKit() {
        try {
            // Inicializar detector ML
            mlFallDetector = new MLFallDetector(this, this);
            
            // Inicializar analizador de patrones
            motionAnalyzer = new MotionPatternAnalyzer();
            
            Log.d(TAG, "ML Kit inicializado correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al inicializar ML Kit", e);
        }
    }
    
    private void startSensorMonitoring() {
        if (sensorManager != null && accelerometer != null && fallDetector != null && shakeDetector != null) {
            sensorManager.registerListener(fallDetector, accelerometer, 
                SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(shakeDetector, accelerometer, 
                SensorManager.SENSOR_DELAY_UI);
            
            // Iniciar detección ML
            if (mlFallDetector != null) {
                mlFallDetector.startMLDetection();
            }
            
            Log.d(TAG, "Monitoreo de sensores y ML iniciado");
        } else {
            Log.e(TAG, "No se puede iniciar monitoreo - sensores no disponibles");
        }
    }
    
    private void stopSensorMonitoring() {
        if (sensorManager != null) {
            if (fallDetector != null) {
                sensorManager.unregisterListener(fallDetector);
            }
            if (shakeDetector != null) {
                sensorManager.unregisterListener(shakeDetector);
            }
            
            // Detener detección ML
            if (mlFallDetector != null) {
                mlFallDetector.stopMLDetection();
            }
            
            Log.d(TAG, "Monitoreo de sensores y ML detenido");
        }
    }
    
    private void startForegroundService() {
        try {
            Intent notificationIntent = new Intent(this, com.example.fallalarm.ui.MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.service_notification_title))
                .setContentText(getString(R.string.service_notification_text))
                .setSmallIcon(R.drawable.ic_security)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();
            
            startForeground(NOTIFICATION_ID, notification);
            Log.d(TAG, "Servicio en primer plano iniciado correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al iniciar servicio en primer plano", e);
        }
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                getString(R.string.service_channel_name),
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription(getString(R.string.service_channel_description));
            channel.setShowBadge(false);
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
    
    @Override
    public void onFallDetected() {
        Log.w(TAG, "Caída detectada!");
        triggerEmergency();
    }
    
    @Override
    public void onShakeDetected() {
        Log.w(TAG, "Sacudida detectada!");
        triggerEmergency();
    }
    
    @Override
    public void onMLFallDetected(float confidence) {
        Log.w(TAG, "ML: Caída detectada con confianza: " + confidence);
        triggerEmergency();
    }
    
    @Override
    public void onMLMotionDetected(String motionType, float confidence) {
        Log.d(TAG, "ML: Movimiento detectado - " + motionType + " (confianza: " + confidence + ")");
        // Aquí se podría implementar lógica adicional según el tipo de movimiento
    }
    
    private void triggerEmergency() {
        try {
            // Vibración breve de confirmación
            if (vibrator != null) {
                vibrator.vibrate(200);
            }
            
            // Iniciar actividad de emergencia
            Intent emergencyIntent = new Intent(this, EmergencyActivity.class);
            emergencyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | 
                                   Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(emergencyIntent);
            
            Log.i(TAG, "Emergencia activada - Pantalla de emergencia mostrada");
        } catch (Exception e) {
            Log.e(TAG, "Error al activar emergencia", e);
        }
    }
}
