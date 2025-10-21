package com.example.fallalarm.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

/**
 * Clase para manejar la reproducción de sonidos de alarma
 */
public class SoundPlayer {
    
    private static final String TAG = "SoundPlayer";
    
    private Context context;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    
    public SoundPlayer(Context context) {
        this.context = context;
    }
    
    /**
     * Reproduce la alarma de forma continua
     */
    public void playAlarm() {
        if (isPlaying) {
            return; // Ya está reproduciendo
        }
        
        try {
            // Obtener el tono de alarma del sistema
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                // Si no hay tono de alarma, usar notificación
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            
            if (alarmUri == null) {
                Log.e(TAG, "No se pudo obtener tono de alarma");
                return;
            }
            
            // Liberar MediaPlayer anterior si existe
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
            
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(context, alarmUri);
            
            // Configurar para reproducción en bucle
            mediaPlayer.setLooping(true);
            
            // Configurar atributos de audio para Android 5.0+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                    .build();
                mediaPlayer.setAudioAttributes(audioAttributes);
            } else {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            }
            
            // Configurar el volumen al máximo
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);
            }
            
            mediaPlayer.prepare();
            mediaPlayer.start();
            
            isPlaying = true;
            Log.d(TAG, "Alarma iniciada");
            
        } catch (Exception e) {
            Log.e(TAG, "Error al reproducir alarma", e);
            isPlaying = false;
            // Intentar liberar recursos en caso de error
            if (mediaPlayer != null) {
                try {
                    mediaPlayer.release();
                } catch (Exception ex) {
                    Log.e(TAG, "Error al liberar MediaPlayer", ex);
                }
                mediaPlayer = null;
            }
        }
    }
    
    /**
     * Detiene la alarma
     */
    public void stopAlarm() {
        if (mediaPlayer != null && isPlaying) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                isPlaying = false;
                Log.d(TAG, "Alarma detenida");
            } catch (Exception e) {
                Log.e(TAG, "Error al detener alarma", e);
            }
        }
    }
    
    /**
     * Verifica si la alarma está reproduciéndose
     */
    public boolean isPlaying() {
        return isPlaying;
    }
    
    /**
     * Libera los recursos
     */
    public void release() {
        stopAlarm();
    }
}
