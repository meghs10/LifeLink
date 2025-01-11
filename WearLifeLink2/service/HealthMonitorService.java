package com.example.wearlifelink.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.health.services.client.HealthServicesClient;
import androidx.health.services.client.MeasureCallback;
import androidx.health.services.client.MeasureClient;
import androidx.health.services.client.data.Availability;
import androidx.health.services.client.data.DataPoint;
import androidx.health.services.client.data.DataType;
import androidx.health.services.client.data.DeltaDataType;
import androidx.wear.ongoing.OngoingActivity;
import androidx.wear.ongoing.Status;

import com.example.wearlifelink.R;
import com.example.wearlifelink.util.DatabaseHelper;
import com.google.android.gms.tasks.Tasks;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HealthMonitorService extends Service {
    private static final String TAG = "HealthMonitorService";
    private static final String CHANNEL_ID = "health_monitor";
    private static final int NOTIFICATION_ID = 1;

    public static final String ACTION_START = "START_MONITORING";
    public static final String ACTION_STOP = "STOP_MONITORING";

    private HealthServicesClient healthClient;
    private MeasureClient measureClient;
    private AlertService alertService;
    private DatabaseHelper dbHelper;
    private ExecutorService executorService;
    private boolean isMonitoring = false;

    // Health thresholds
    private static final int HEART_RATE_MIN = 40;
    private static final int HEART_RATE_MAX = 120;
    private static final float TEMPERATURE_MIN = 35.0f;
    private static final float TEMPERATURE_MAX = 39.0f;
    private static final int BLOOD_OXYGEN_MIN = 90;

    private final MeasureCallback measureCallback = new MeasureCallback() {
        @Override
        public void onAvailabilityChanged(@NonNull DeltaDataType<?, ?> dataType,
                                        @NonNull Availability availability) {
            Log.i(TAG, "Data type " + dataType + " availability changed to: " + availability);
        }

        @Override
        public void onDataReceived(@NonNull DataPoint dataPoint) {
            executorService.execute(() -> processHealthData(dataPoint));
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        healthClient = HealthServicesClient.getClient(this);
        measureClient = healthClient.getMeasureClient();
        alertService = new AlertService(this);
        dbHelper = new DatabaseHelper(this);
        executorService = Executors.newSingleThreadExecutor();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Health Monitoring",
                NotificationManager.IMPORTANCE_LOW);
        channel.setDescription("Ongoing health monitoring service");
        
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_START.equals(action) && !isMonitoring) {
                startMonitoring();
            } else if (ACTION_STOP.equals(action)) {
                stopMonitoring();
                stopForeground(true);
                stopSelf();
            }
        }
        return START_STICKY;
    }

    private void startMonitoring() {
        executorService.execute(() -> {
            try {
                // Check if the required data types are available
                Set<DataType> supportedTypes = Tasks.await(measureClient.getSupportedDataTypes());
                
                if (!supportedTypes.contains(DataType.HEART_RATE_BPM)) {
                    Log.w(TAG, "Heart rate monitoring not supported");
                    return;
                }

                // Register for updates
                Tasks.await(measureClient.registerMeasureCallback(
                    supportedTypes,
                    measureCallback
                ));

                isMonitoring = true;
                startForeground(NOTIFICATION_ID, createNotification());
                Log.i(TAG, "Successfully started health monitoring");

            } catch (Exception e) {
                Log.e(TAG, "Failed to start health monitoring", e);
            }
        });
    }

    private Notification createNotification() {
        OngoingActivity ongoingActivity = new OngoingActivity(
                this,
                NOTIFICATION_ID,
                createNotificationBuilder().build()
        );

        Status status = new Status.Builder()
                .addTemplate("Monitoring vital signs")
                .build();
        ongoingActivity.setStatus(status);
        ongoingActivity.apply(this);

        return createNotificationBuilder().build();
    }

    private NotificationCompat.Builder createNotificationBuilder() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Health Monitoring")
                .setContentText("Monitoring vital signs")
                .setSmallIcon(R.drawable.ic_alert)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    private void stopMonitoring() {
        if (isMonitoring) {
            executorService.execute(() -> {
                try {
                    Tasks.await(measureClient.unregisterMeasureCallback(measureCallback));
                    isMonitoring = false;
                    Log.i(TAG, "Stopped health monitoring");
                } catch (Exception e) {
                    Log.e(TAG, "Error stopping health monitoring", e);
                }
            });
        }
    }

    private void processHealthData(DataPoint dataPoint) {
        long timestamp = System.currentTimeMillis();
        DataType type = dataPoint.getDataType();

        try {
            if (type == DataType.HEART_RATE_BPM) {
                int heartRate = dataPoint.getValue().asInt();
                checkHeartRate(heartRate);
                saveHealthData(timestamp, "heart_rate", heartRate);
                broadcastHealthData("heart_rate", heartRate);
            } else if (type == DataType.BLOOD_OXYGEN_SATURATION) {
                int bloodOxygen = dataPoint.getValue().asInt();
                checkBloodOxygen(bloodOxygen);
                saveHealthData(timestamp, "blood_oxygen", bloodOxygen);
                broadcastHealthData("blood_oxygen", bloodOxygen);
            } else if (type == DataType.BODY_TEMPERATURE) {
                float temperature = dataPoint.getValue().asFloat();
                checkTemperature(temperature);
                saveHealthData(timestamp, "temperature", temperature);
                broadcastHealthData("temperature", (int)(temperature * 100));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error processing health data", e);
        }
    }

    // ... rest of your methods (checkHeartRate, checkBloodOxygen, etc.) remain the same ...

    @Override
    public void onDestroy() {
        stopMonitoring();
        executorService.shutdownNow();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}