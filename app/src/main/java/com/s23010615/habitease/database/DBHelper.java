package com.s23010615.habitease.database;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.s23010615.habitease.models.Habit;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    // Database Info
    private static final String DATABASE_NAME = "HabitEase.db";
    private static final int DATABASE_VERSION = 2; // Incremented version for schema changes

    // Users Table
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    // Habits Table
    public static final String TABLE_HABITS = "habits";
    public static final String COLUMN_HABIT_ID = "id";
    public static final String COLUMN_HABIT_NAME = "name";
    public static final String COLUMN_HABIT_TIME = "time";
    public static final String COLUMN_HOME_ONLY = "homeOnly";
    public static final String COLUMN_IS_COMPLETED = "isCompleted";
    public static final String COLUMN_STREAK_COUNT = "streakCount";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String createUserTable = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createUserTable);

        // Create habits table
        String createHabitTable = "CREATE TABLE IF NOT EXISTS " + TABLE_HABITS + " (" +
                COLUMN_HABIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_HABIT_NAME + " TEXT, " +
                COLUMN_HABIT_TIME + " TEXT, " +
                COLUMN_HOME_ONLY + " INTEGER DEFAULT 0, " +
                COLUMN_IS_COMPLETED + " INTEGER DEFAULT 0, " +
                COLUMN_STREAK_COUNT + " INTEGER DEFAULT 0)";
        db.execSQL(createHabitTable);

        // Create location table
        String createLocationTable = "CREATE TABLE IF NOT EXISTS home_location (" +
                "id INTEGER PRIMARY KEY, " +
                "latitude REAL, " +
                "longitude REAL)";
        db.execSQL(createLocationTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add streakCount column
            db.execSQL("ALTER TABLE " + TABLE_HABITS +
                    " ADD COLUMN " + COLUMN_STREAK_COUNT + " INTEGER DEFAULT 0");
        }
        if (oldVersion < 3) {
            db.execSQL("DROP TABLE IF EXISTS home_location");
            db.execSQL("CREATE TABLE IF NOT EXISTS home_location (" +
                    "id INTEGER PRIMARY KEY, " +
                    "latitude REAL, " +
                    "longitude REAL)");
        }
    }

    // User methods
    public boolean insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values) != -1;
    }

    public boolean validateCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_USERS +
                        " WHERE " + COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password})) {
            return cursor.getCount() > 0;
        }
    }

    public boolean isUsernameTaken(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=? LIMIT 1",
                new String[]{username})) {
            return cursor.getCount() > 0;
        }
    }

    // Habit methods
    public long addHabit(Habit habit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HABIT_NAME, habit.getName());
        values.put(COLUMN_HABIT_TIME, habit.getTime());
        values.put(COLUMN_HOME_ONLY, habit.isHomeOnly() ? 1 : 0);
        values.put(COLUMN_IS_COMPLETED, habit.isCompleted() ? 1 : 0);
        values.put(COLUMN_STREAK_COUNT, habit.getStreakCount());
        return db.insert(TABLE_HABITS, null, values);
    }

    public List<Habit> getAllHabits() {
        List<Habit> habits = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try (Cursor cursor = db.query(TABLE_HABITS,
                null, null, null, null, null, null)) {

            if (cursor.moveToFirst()) {
                do {
                    Habit habit = new Habit(
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HABIT_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HABIT_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HABIT_TIME)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HOME_ONLY)) == 1,
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1,
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STREAK_COUNT))
                    );
                    habits.add(habit);
                } while (cursor.moveToNext());
            }
        }
        return habits;
    }

    public void updateHabitCompletion(int id, boolean completed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_COMPLETED, completed ? 1 : 0);
        db.update(TABLE_HABITS, values,
                COLUMN_HABIT_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int updateHabitStreak(int id, int streakCount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STREAK_COUNT, streakCount);
        return db.update(TABLE_HABITS, values,
                COLUMN_HABIT_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int deleteHabit(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_HABITS,
                COLUMN_HABIT_ID + "=?", new String[]{String.valueOf(id)});
    }

    public boolean saveHomeLocation(double lat, double lng) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", 1);
        values.put("latitude", lat);
        values.put("longitude", lng);

        // Store only one row – you can extend user-specific logic later
        // allows to store only one home location
        long result = db.replace("home_location", null, values);
        Log.d("DBHelper", "Replace result: " + result);
        return result != -1;
    }
    public LatLng getSavedHomeLocation() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT latitude, longitude FROM home_location WHERE id = 1", null);
        if (cursor != null && cursor.moveToFirst()) {
            double lat = cursor.getDouble(0);
            double lng = cursor.getDouble(1);
            cursor.close();
            return new LatLng(lat, lng);
        }
        return null;
    }
    // Get home location as Android Location
    public Location getHomeLocation() {
        LatLng latLng = getSavedHomeLocation();
        if (latLng == null) return null;
        Location loc = new Location("");
        loc.setLatitude(latLng.latitude);
        loc.setLongitude(latLng.longitude);
        return loc;
    }

}