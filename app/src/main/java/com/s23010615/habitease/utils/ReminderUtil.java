package com.s23010615.habitease.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import com.s23010615.habitease.receivers.HabitReminderReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReminderUtil {

    // Expecting time in "hh:mm a" format (e.g., "08:30 AM", "07:45 PM")
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    @RequiresPermission(android.Manifest.permission.SCHEDULE_EXACT_ALARM)
    public static void scheduleDailyReminder(Context context, String habitName, String time, int habitId, boolean isHomeOnly) {
        Calendar alarmTime = Calendar.getInstance();
        try {
            Date parsedTime = TIME_FORMAT.parse(time);
            if (parsedTime != null) {
                Calendar now = Calendar.getInstance();
                alarmTime.setTime(parsedTime);
                alarmTime.set(Calendar.YEAR, now.get(Calendar.YEAR));
                alarmTime.set(Calendar.MONTH, now.get(Calendar.MONTH));
                alarmTime.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));

                // If time already passed today, schedule for tomorrow
                if (alarmTime.before(now)) {
                    alarmTime.add(Calendar.DAY_OF_MONTH, 1);
                }
            }
        } catch (ParseException e) {
            Log.e("ReminderUtil", "Invalid time format: " + time);
            alarmTime.add(Calendar.MINUTE, 1); // fallback
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, HabitReminderReceiver.class);
        intent.putExtra("habitName", habitName);
        intent.putExtra("habitId", habitId);
        intent.putExtra("isHomeOnly", isHomeOnly);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                context,
                habitId, // unique ID per habit
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : 0)
        );

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), alarmIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), alarmIntent);
            }

            Log.d("ReminderUtil", "Alarm set for: " + TIME_FORMAT.format(alarmTime.getTime()));
        }
    }

    public static void cancelReminder(Context context, int habitId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, HabitReminderReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                context,
                habitId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : 0)
        );

        if (alarmManager != null) {
            alarmManager.cancel(alarmIntent);
            Log.d("ReminderUtil", "Reminder canceled for habit ID: " + habitId);
        }
    }
}



