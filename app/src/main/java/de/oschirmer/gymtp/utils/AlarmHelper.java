package de.oschirmer.gymtp.utils;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.Settings;

import de.oschirmer.gymtp.coverplan.fetch.FetchAlarmReceiver;

public class AlarmHelper {

    private final Context context;
    private final AlarmManager alarmManager;

    public AlarmHelper(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void checkAndRequestExactAlarmPermission() {
        if (!alarmManager.canScheduleExactAlarms()) {
            showPermissionRationaleDialog();
        } else {
            // permission is already granted, so schedule the exact alarm
            scheduleExactAlarm();
        }
    }

    private void showPermissionRationaleDialog() {
        new AlertDialog.Builder(context)
                .setTitle("Erlaubnis für exakte Alarme erteilen")
                .setMessage("Um rechtzeitige Aktualisierungen und Hintergrundabfragen sicherzustellen, benötigt diese App die Berechtigung zur genauen Alarmplanung. Ohne diese Erlaubnis können einige Funktionen wie Benachrichtigungen oder Datensynchronisierungen verzögert werden. Bitte erteilen Sie diese Erlaubnis in den App-Einstellungen.")
                .setPositiveButton("Zu den Einstellungen", (dialog, which) -> {
                    // redirect to settings page for permission granting
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:" + context.getPackageName()));
                    context.startActivity(intent);
                })
                .setNegativeButton("Abbrechen", (dialog, which) -> {
                    // user denied permission, so use inexact alarm scheduling
                    scheduleInexactAlarm();
                })
                .show();
    }

    public void scheduleExactAlarm() {
        Intent alarmIntent = new Intent(context, FetchAlarmReceiver.class);
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(),
                FetchAlarmReceiver.ALARM_ID,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (!alarmManager.canScheduleExactAlarms()) {
            scheduleInexactAlarm();
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + FetchAlarmReceiver.FETCH_TIME, pendingAlarmIntent);
        }
    }

    public void scheduleInexactAlarm() {
        Intent alarmIntent = new Intent(context, FetchAlarmReceiver.class);
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(),
                FetchAlarmReceiver.ALARM_ID,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + FetchAlarmReceiver.FETCH_TIME, pendingAlarmIntent);
    }
}
