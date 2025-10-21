package com.example.fallalarm.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

/**
 * Utilidades para manejo de permisos
 */
public class PermissionUtils {
    
    /**
     * Verifica si se tienen todos los permisos necesarios
     */
    public static boolean hasAllPermissions(Context context) {
        return hasNotificationPermission(context) && hasVibratePermission(context);
    }
    
    /**
     * Verifica si se tiene permiso de notificaciones (Android 13+)
     */
    public static boolean hasNotificationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) 
                == PackageManager.PERMISSION_GRANTED;
        }
        return true; // No se requiere en versiones anteriores
    }
    
    /**
     * Verifica si se tiene permiso de vibración
     */
    public static boolean hasVibratePermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.VIBRATE) 
            == PackageManager.PERMISSION_GRANTED;
    }
    
    /**
     * Obtiene los permisos que faltan
     */
    public static String[] getMissingPermissions(Context context) {
        java.util.List<String> missingPermissions = new java.util.ArrayList<>();
        
        if (!hasNotificationPermission(context)) {
            missingPermissions.add(Manifest.permission.POST_NOTIFICATIONS);
        }
        
        if (!hasVibratePermission(context)) {
            missingPermissions.add(Manifest.permission.VIBRATE);
        }
        
        return missingPermissions.toArray(new String[0]);
    }
}
