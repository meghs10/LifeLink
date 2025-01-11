package com.example.wearlifelink.util;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "HealthMonitor.db";
    private static final int DATABASE_VERSION = 1;

    // Tables
    public static final String TABLE_CONTACTS = "emergency_contacts";
    public static final String TABLE_HEALTH_DATA = "health_data";

    // Common columns
    public static final String COLUMN_ID = "_id";

    // Contact columns
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_IS_PRIMARY = "is_primary";

    // Health data columns
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_HEART_RATE = "heart_rate";
    public static final String COLUMN_TEMPERATURE = "temperature";
    public static final String COLUMN_BLOOD_OXYGEN = "blood_oxygen";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create contacts table
        String createContactsTable = "CREATE TABLE " + TABLE_CONTACTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_PHONE + " TEXT NOT NULL, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_IS_PRIMARY + " INTEGER DEFAULT 0)";

        // Create health data table
        String createHealthDataTable = "CREATE TABLE " + TABLE_HEALTH_DATA + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIMESTAMP + " INTEGER NOT NULL, " +
                COLUMN_HEART_RATE + " INTEGER, " +
                COLUMN_TEMPERATURE + " REAL, " +
                COLUMN_BLOOD_OXYGEN + " INTEGER)";

        db.execSQL(createContactsTable);
        db.execSQL(createHealthDataTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle all possible upgrade paths
        if (oldVersion < 2) {
            // Upgrade to version 2
            // Example: Add a new column to contacts table
            try {
                db.execSQL("ALTER TABLE " + TABLE_CONTACTS +
                        " ADD COLUMN last_updated INTEGER DEFAULT 0");
            } catch (Exception e) {
                // Log error or handle gracefully
            }
        }

        if (oldVersion < 3) {
            // Upgrade to version 3
            // Example: Add a new table for medical conditions
            try {
                db.execSQL("CREATE TABLE IF NOT EXISTS medical_conditions (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "condition TEXT NOT NULL, " +
                        "severity TEXT, " +
                        "diagnosed_date INTEGER)");
            } catch (Exception e) {
                // Log error or handle gracefully
            }
        }

        if (oldVersion < 4) {
            // Upgrade to version 4
            // Example: Add new columns to health_data table
            try {
                db.execSQL("ALTER TABLE " + TABLE_HEALTH_DATA +
                        " ADD COLUMN blood_pressure_systolic INTEGER");
                db.execSQL("ALTER TABLE " + TABLE_HEALTH_DATA +
                        " ADD COLUMN blood_pressure_diastolic INTEGER");
            } catch (Exception e) {
                // Log error or handle gracefully
            }
        }

        // If you need to do any data migration
        migrateData(db, oldVersion, newVersion);
    }

    private void migrateData(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Example of data migration
        if (oldVersion < 2) {
            // Update the last_updated field for existing contacts
            long currentTime = System.currentTimeMillis();
            ContentValues values = new ContentValues();
            values.put("last_updated", currentTime);
            db.update(TABLE_CONTACTS, values, null, null);
        }

        // Add more data migrations as needed
    }

    // Optional: Method to handle downgrade (reverting to an older version)
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // You could either:
        // 1. Throw an exception to prevent downgrade
        // throw new SQLiteException("Can't downgrade database from version " +
        //     oldVersion + " to " + newVersion);

        // 2. Or reset the database
        resetDatabase(db);
    }

    // Helper method to reset the database
    private void resetDatabase(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HEALTH_DATA);
        db.execSQL("DROP TABLE IF EXISTS medical_conditions");
        onCreate(db);
    }

    public void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HEALTH_DATA);
        onCreate(db);
    }

    // Helper method to check if a primary contact exists
    public boolean hasPrimaryContact() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_CONTACTS +
                " WHERE " + COLUMN_IS_PRIMARY + "=1";
        try (android.database.Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0) > 0;
            }
        }
        return false;
    }

    // Helper method to get the latest health data
    public android.database.Cursor getLatestHealthData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_HEALTH_DATA, null, null, null, null, null,
                COLUMN_TIMESTAMP + " DESC", "1");
    }

    // Helper method to get health data within a time range
    public android.database.Cursor getHealthDataInRange(long startTime, long endTime) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_TIMESTAMP + " BETWEEN ? AND ?";
        String[] selectionArgs = {String.valueOf(startTime), String.valueOf(endTime)};
        return db.query(TABLE_HEALTH_DATA, null, selection, selectionArgs, null, null,
                COLUMN_TIMESTAMP + " DESC");
    }

}
