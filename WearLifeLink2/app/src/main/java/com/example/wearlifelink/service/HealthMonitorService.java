package com.example.wearlifelink.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.health.services.client.HealthServices;
import androidx.health.services.client.HealthServicesClient;
import androidx.health.services.client.MeasureCallback;
import androidx.health.services.client.MeasureClient;
import androidx.health.services.client.PassiveListenerCallback;
import androidx.health.services.client.PassiveMonitoringClient;
import androidx.health.services.client.data.Availability;
import androidx.health.services.client.data.DataPointContainer;
import androidx.health.services.client.data.DataType;
import androidx.health.services.client.data.DataTypeAvailability;
import androidx.health.services.client.data.DeltaDataType;
import androidx.health.services.client.data.SampleDataPoint;

import com.example.wearlifelink.R;
import com.example.wearlifelink.util.DatabaseHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HealthMonitorService extends Service {
    private static final String TAG = "HealthMonitorService";
    private static final String CHANNEL_ID = "health_monitor";
    private static final int NOTIFICATION_ID = 1;

    public static final String ACTION_START = "START_MONITORING";
    public static final String ACTION_STOP = "STOP_MONITORING";

    private HealthServicesClient healthServicesClient;
    private MeasureClient measureClient;
    private PassiveMonitoringClient passiveMonitoringClient;
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
        public void onDataReceived(@NonNull DataPointContainer dataPoints) {
            executorService.execute(() -> {
                try {
                    // Get sample data points
                    for (SampleDataPoint dataPoint : dataPoints.getSampleDataPoints()) {
                        processHealthData(dataPoint);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing data points", e);
                }
            });
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        healthServicesClient = HealthServices.getClient(this);
        measureClient = healthServicesClient.getMeasureClient();
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
        channel.setShowBadge(false);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

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
        if (!isMonitoring) {
            executorService.execute(() -> {
                try {
                    // Register for heart rate updates
                    measureClient.registerMeasureCallback(
                            DeltaDataType.HEART_RATE_BPM,
                            measureCallback
                    );

                    isMonitoring = true;
                    startForeground(NOTIFICATION_ID, createNotification());
                    Log.i(TAG, "Successfully started health monitoring");
                } catch (Exception e) {
                    Log.e(TAG, "Failed to start health monitoring", e);
                }
            });
        }
    }

    private Notification createNotification() {
        Intent stopIntent = new Intent(this, HealthMonitorService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getService(
                this,
                0,
                stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Health Monitoring")
                .setContentText("Monitoring vital signs")
                .setSmallIcon(R.drawable.ic_alert)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .addAction(0, "Stop Monitoring", stopPendingIntent)
                .build();
    }
    private void updateNotification(String message) {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Health Monitoring")
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_alert)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private void stopMonitoring() {
        if (isMonitoring) {
            executorService.execute(() -> {
                try {
                    // Just set monitoring to false and let the service stop
                    isMonitoring = false;
                    Log.i(TAG, "Stopped health monitoring");
                } catch (Exception e) {
                    Log.e(TAG, "Error stopping health monitoring", e);
                }
            });
        }
    }

    private void processHealthData(SampleDataPoint dataPoint) {
        long timestamp = System.currentTimeMillis();

        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_TIMESTAMP, timestamp);

            if (dataPoint.getDataType() == DeltaDataType.HEART_RATE_BPM) {
                // Get the heart rate value
                int heartRate = (int) dataPoint.getValue();

                checkHeartRate(heartRate);
                values.put(DatabaseHelper.COLUMN_HEART_RATE, heartRate);
                saveHealthData(values);
                broadcastHealthData("heart_rate", heartRate);
                updateNotification("Heart Rate: " + heartRate + " BPM");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error processing health data: " + e.getMessage(), e);
        }
    }

    private void saveHealthData(ContentValues values) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.insert(DatabaseHelper.TABLE_HEALTH_DATA, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error saving health data to database", e);
        }
    }

    private void getLatestHealthData() {
        try (Cursor cursor = dbHelper.getLatestHealthData()) {
            if (cursor != null && cursor.moveToFirst()) {
                int heartRate = cursor.getInt(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HEART_RATE));
                float temperature = cursor.getFloat(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TEMPERATURE));
                int bloodOxygen = cursor.getInt(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BLOOD_OXYGEN));

                updateLatestReadings(heartRate, temperature, bloodOxygen);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving latest health data", e);
        }
    }

    private void updateLatestReadings(int heartRate, float temperature, int bloodOxygen) {
        Intent intent = new Intent("HEALTH_DATA_UPDATE");
        intent.putExtra("heart_rate", heartRate);
        intent.putExtra("temperature", (int)(temperature * 100));
        intent.putExtra("blood_oxygen", bloodOxygen);
        intent.putExtra("timestamp", System.currentTimeMillis());
        sendBroadcast(intent);
    }

    private void checkHeartRate(int heartRate) {
        if (heartRate < HEART_RATE_MIN || heartRate > HEART_RATE_MAX) {
            alertService.sendHealthAlert(
                    "Abnormal Heart Rate",
                    "Your heart rate is " + heartRate + " BPM"
            );
        }
    }

    private void checkBloodOxygen(int bloodOxygen) {
        if (bloodOxygen < BLOOD_OXYGEN_MIN) {
            alertService.sendHealthAlert(
                    "Low Blood Oxygen",
                    "Your blood oxygen level is " + bloodOxygen + "%"
            );
        }
    }

    private void checkTemperature(float temperature) {
        if (temperature < TEMPERATURE_MIN || temperature > TEMPERATURE_MAX) {
            alertService.sendHealthAlert(
                    "Abnormal Temperature",
                    "Your body temperature is " + temperature + "°C"
            );
        }
    }

    private void broadcastHealthData(String type, int value) {
        Intent intent = new Intent("HEALTH_DATA_UPDATE");
        intent.putExtra("type", type);
        intent.putExtra("value", value);
        intent.putExtra("timestamp", System.currentTimeMillis());
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        if (isMonitoring) {
            try {
                // The service destruction will automatically clean up the callbacks
                isMonitoring = false;
            } catch (Exception e) {
                Log.e(TAG, "Error on destroy", e);
            }
        }
        executorService.shutdownNow();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}