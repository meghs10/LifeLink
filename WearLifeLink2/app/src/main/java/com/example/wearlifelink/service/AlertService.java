package com.example.wearlifelink.service;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import com.example.wearlifelink.R;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.wearlifelink.MainActivity;
import com.example.wearlifelink.util.DatabaseHelper;

import java.util.ArrayList;

public class AlertService {
    private static final String TAG = "AlertService";
    private Context context;
    private NotificationManager notificationManager;
    private DatabaseHelper dbHelper;

    // Notification channels
    private static final String CHANNEL_ID = "health_alerts";
    private static final String EMERGENCY_CHANNEL_ID = "emergency_alerts";
    private static final int NOTIFICATION_ID = 1;
    private static final int EMERGENCY_NOTIFICATION_ID = 2;

    public AlertService(Context context) {
        this.context = context;
        this.notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.dbHelper = new DatabaseHelper(context);
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        // Regular health alerts channel
        NotificationChannel healthChannel = new NotificationChannel(
                CHANNEL_ID,
                "Health Alerts",
                NotificationManager.IMPORTANCE_HIGH
        );
        healthChannel.setDescription("Alerts for abnormal health readings");
        healthChannel.enableVibration(true);
        healthChannel.setVibrationPattern(new long[]{0, 500, 250, 500});

        // Emergency alerts channel
        NotificationChannel emergencyChannel = new NotificationChannel(
                EMERGENCY_CHANNEL_ID,
                "Emergency Alerts",
                NotificationManager.IMPORTANCE_HIGH
        );
        emergencyChannel.setDescription("Critical health emergency alerts");
        emergencyChannel.enableVibration(true);
        emergencyChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});

        notificationManager.createNotificationChannel(healthChannel);
        notificationManager.createNotificationChannel(emergencyChannel);
    }

    public void sendHealthAlert(String title, String message) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alert)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void sendEmergencyAlert(String message) {
        // Show emergency notification
        showEmergencyNotification(message);

        // Send emergency SMS
        sendEmergencySMS(message);
    }

    private void showEmergencyNotification(String message) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, EMERGENCY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_emergency)
                .setContentTitle("Emergency Alert!")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOngoing(true); // Makes the notification persistent

        notificationManager.notify(EMERGENCY_NOTIFICATION_ID, builder.build());
    }

    private void sendEmergencySMS(String message) {
        // Get primary contact from database
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_CONTACTS,
                    new String[]{DatabaseHelper.COLUMN_PHONE},
                    DatabaseHelper.COLUMN_IS_PRIMARY + "=1",
                    null, null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                String phoneNumber = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE)
                );

                // Send SMS
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    ArrayList<String> parts = smsManager.divideMessage(message);
                    smsManager.sendMultipartTextMessage(
                            phoneNumber,
                            null,
                            parts,
                            null,
                            null
                    );
                    Log.i(TAG, "Emergency SMS sent to: " + phoneNumber);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to send emergency SMS", e);
                }
            }

            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error accessing database", e);
        }
    }

    // Helper method to check if vitals are in dangerous range
    public boolean isVitalSignsCritical(int heartRate, float temperature, int bloodOxygen) {
        return heartRate > 120 || heartRate < 40 ||        // Dangerous heart rate
                temperature > 39.0 || temperature < 35.0 ||  // Dangerous temperature
                bloodOxygen < 90;                           // Low blood oxygen
    }

    // Method to handle different types of health alerts
    public void handleHealthAlert(int heartRate, float temperature, int bloodOxygen) {
        StringBuilder message = new StringBuilder("Health Alert: ");
        boolean isCritical = false;

        if (heartRate > 120 || heartRate < 40) {
            message.append("Abnormal heart rate: ").append(heartRate).append(" BPM. ");
            isCritical = true;
        }
        if (temperature > 39.0 || temperature < 35.0) {
            message.append("Abnormal temperature: ").append(temperature).append("°C. ");
            isCritical = true;
        }
        if (bloodOxygen < 90) {
            message.append("Low blood oxygen: ").append(bloodOxygen).append("%. ");
            isCritical = true;
        }

        if (isCritical) {
            sendEmergencyAlert(message.toString());
        } else if (heartRate > 100 || heartRate < 50 ||
                temperature > 38.0 || temperature < 36.0 ||
                bloodOxygen < 95) {
            sendHealthAlert("Health Warning", message.toString());
        }
    }
}