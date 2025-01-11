package com.example.wearlifelink;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import com.example.wearlifelink.databinding.ActivityHealthMonitorBinding;
import com.example.wearlifelink.service.HealthMonitorService;

public class HealthMonitorActivity extends FragmentActivity {
    private ActivityHealthMonitorBinding binding;
    private HealthDataReceiver healthDataReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHealthMonitorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupUI();
        registerReceiver();
    }

    private void setupUI() {
        binding.startMonitoringButton.setOnClickListener(v -> {
            Intent serviceIntent = new Intent(this, HealthMonitorService.class);
            serviceIntent.setAction("START_MONITORING");
            startService(serviceIntent);
            binding.startMonitoringButton.setEnabled(false);
            binding.stopMonitoringButton.setEnabled(true);
        });

        binding.stopMonitoringButton.setOnClickListener(v -> {
            Intent serviceIntent = new Intent(this, HealthMonitorService.class);
            serviceIntent.setAction("STOP_MONITORING");
            startService(serviceIntent);
            binding.startMonitoringButton.setEnabled(true);
            binding.stopMonitoringButton.setEnabled(false);
        });
    }

    private void registerReceiver() {
        healthDataReceiver = new HealthDataReceiver();
        IntentFilter filter = new IntentFilter("HEALTH_DATA_UPDATE");
        registerReceiver(healthDataReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
    }

    private class HealthDataReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            int value = intent.getIntExtra("value", 0);

            if ("heart_rate".equals(type)) {
                binding.heartRateText.setText("Heart Rate: " + value + " BPM");
            }
            // Handle other health metrics
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(healthDataReceiver);
    }
}