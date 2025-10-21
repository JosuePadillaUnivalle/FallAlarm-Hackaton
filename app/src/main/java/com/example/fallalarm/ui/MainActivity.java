package com.example.fallalarm.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fallalarm.R;
import com.example.fallalarm.broadcast.BootReceiver;
import com.example.fallalarm.service.FallAlarmService;
import com.example.fallalarm.util.PermissionUtils;

public class MainActivity extends AppCompatActivity {

    private Button surveillanceButton;
    private TextView statusText;
    private boolean isSurveillanceActive = false;
    
    private ActivityResultLauncher<String> notificationPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupPermissionLauncher();
        updateUI();
        
        // Verificar si el servicio ya está activo
        checkServiceStatus();
    }

    private void initializeViews() {
        surveillanceButton = findViewById(R.id.surveillance_button);
        statusText = findViewById(R.id.status_text);
        
        surveillanceButton.setOnClickListener(v -> toggleSurveillance());
    }

    private void setupPermissionLauncher() {
        notificationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    toggleSurveillance();
                } else {
                    Toast.makeText(this, getString(R.string.notification_permission_required), 
                        Toast.LENGTH_LONG).show();
                }
            }
        );
    }

    private void toggleSurveillance() {
        if (isSurveillanceActive) {
            stopSurveillance();
        } else {
            startSurveillance();
        }
    }

    private void startSurveillance() {
        // Verificar permisos de notificación para Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                return;
            }
        }

        try {
            // Verificar permisos de servicio en primer plano
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_SPECIAL_USE) 
                    != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Se requiere permiso de servicio en primer plano", 
                        Toast.LENGTH_LONG).show();
                    return;
                }
            }
            
            // Iniciar el servicio
            Intent serviceIntent = new Intent(this, FallAlarmService.class);
            startForegroundService(serviceIntent);
            
            isSurveillanceActive = true;
            updateUI();
            
            Toast.makeText(this, "Vigilancia activada", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al activar vigilancia: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }

    private void stopSurveillance() {
        try {
            // Detener el servicio
            Intent serviceIntent = new Intent(this, FallAlarmService.class);
            stopService(serviceIntent);
            
            isSurveillanceActive = false;
            updateUI();
            
            Toast.makeText(this, "Vigilancia desactivada", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al desactivar vigilancia: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }

    private void updateUI() {
        if (isSurveillanceActive) {
            surveillanceButton.setText(getString(R.string.deactivate_surveillance));
            surveillanceButton.setBackgroundResource(R.drawable.button_danger_background);
            statusText.setText(getString(R.string.surveillance_active));
            statusText.setTextColor(ContextCompat.getColor(this, R.color.success));
            statusText.setBackgroundResource(R.drawable.status_active_background);
        } else {
            surveillanceButton.setText(getString(R.string.activate_surveillance));
            surveillanceButton.setBackgroundResource(R.drawable.button_primary_background);
            statusText.setText(getString(R.string.surveillance_inactive));
            statusText.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            statusText.setBackgroundResource(R.drawable.status_background);
        }
    }

    private void checkServiceStatus() {
        // Verificar si el servicio está marcado como activo
        isSurveillanceActive = BootReceiver.isServiceActive(this);
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Verificar estado del servicio al volver a la actividad
        checkServiceStatus();
    }
}
