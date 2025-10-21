package com.example.fallalarm.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.fallalarm.service.FallAlarmService;

/**
 * Receptor de broadcast para manejar el arranque del sistema
 * Reanuda el servicio de vigilancia si estaba activo antes del reinicio
 */
public class BootReceiver extends BroadcastReceiver {
    
    private static final String TAG = "BootReceiver";
    private static final String PREFS_NAME = "fall_alarm_prefs";
    private static final String KEY_SERVICE_ACTIVE = "service_active";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "Broadcast recibido: " + action);
        
        if (Intent.ACTION_BOOT_COMPLETED.equals(action) || 
            Intent.ACTION_USER_PRESENT.equals(action)) {
            
            // Verificar si el servicio estaba activo antes del reinicio
            if (wasServiceActive(context)) {
                Log.i(TAG, "Reanudando servicio de vigilancia tras reinicio");
                startFallAlarmService(context);
            } else {
                Log.d(TAG, "Servicio no estaba activo, no se reanuda");
            }
        }
    }
    
    /**
     * Verifica si el servicio estaba activo antes del reinicio
     */
    private boolean wasServiceActive(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_SERVICE_ACTIVE, false);
    }
    
    /**
     * Inicia el servicio de FallAlarm
     */
    private void startFallAlarmService(Context context) {
        try {
            Intent serviceIntent = new Intent(context, FallAlarmService.class);
            context.startForegroundService(serviceIntent);
            Log.i(TAG, "Servicio de vigilancia iniciado tras reinicio");
        } catch (Exception e) {
            Log.e(TAG, "Error al iniciar servicio tras reinicio", e);
        }
    }
    
    /**
     * Marca el servicio como activo (llamado desde el servicio)
     */
    public static void setServiceActive(Context context, boolean active) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_SERVICE_ACTIVE, active).apply();
        Log.d(TAG, "Estado del servicio actualizado: " + active);
    }
    
    /**
     * Verifica si el servicio está marcado como activo
     */
    public static boolean isServiceActive(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_SERVICE_ACTIVE, false);
    }
}
