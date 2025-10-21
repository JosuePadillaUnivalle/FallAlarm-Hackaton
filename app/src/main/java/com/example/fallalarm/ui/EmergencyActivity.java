package com.example.fallalarm.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fallalarm.R;
import com.example.fallalarm.service.FallAlarmService;
import com.example.fallalarm.util.SoundPlayer;

public class EmergencyActivity extends AppCompatActivity {

    private MediaPlayer alarmPlayer;
    private Vibrator vibrator;
    private SoundPlayer soundPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        
        // Mantener la pantalla encendida
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        initializeViews();
        startAlarm();
    }

    private void initializeViews() {
        Button imOkButton = findViewById(R.id.im_ok_button);
        imOkButton.setOnClickListener(v -> stopAlarmAndFinish());
    }

    private void startAlarm() {
        // Iniciar vibración
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            long[] pattern = {0, 1000, 500, 1000, 500, 1000};
            vibrator.vibrate(pattern, 0); // Repetir indefinidamente
        }

        // Iniciar sonido de alarma
        soundPlayer = new SoundPlayer(this);
        soundPlayer.playAlarm();
    }

    private void stopAlarmAndFinish() {
        // Detener vibración
        if (vibrator != null) {
            vibrator.cancel();
        }

        // Detener sonido
        if (soundPlayer != null) {
            soundPlayer.stopAlarm();
        }

        // Registrar evento
        logEmergencyEvent();

        // Cerrar actividad
        finish();
    }

    private void logEmergencyEvent() {
        // Aquí podrías registrar el evento en una base de datos local
        // Por ahora, solo mostramos un mensaje
        android.util.Log.i("FallAlarm", "Usuario confirmó que está bien - " + 
            java.text.DateFormat.getDateTimeInstance().format(new java.util.Date()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Asegurar que se detengan todos los recursos
        if (vibrator != null) {
            vibrator.cancel();
        }
        if (soundPlayer != null) {
            soundPlayer.stopAlarm();
        }
    }

    @Override
    public void onBackPressed() {
        // No permitir salir con el botón atrás durante la emergencia
        // El usuario debe presionar "Estoy bien"
    }
}
