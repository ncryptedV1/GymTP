package de.oschirmer.gymtp.coverplan.fetch;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.oschirmer.gymtp.GtpActivity;
import de.oschirmer.gymtp.R;
import de.oschirmer.gymtp.coverplan.holder.CoverPlanRow;
import de.oschirmer.gymtp.settings.SettingsStore;
import de.oschirmer.gymtp.utils.AlarmHelper;

public class FetchAlarmReceiver extends BroadcastReceiver {

    private Context context;

    public static final int ALARM_ID = 678;
    public static final long FETCH_TIME = 5 * 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        Log.v("fetch", "Started");

        // schedule next background fetch
        AlarmHelper alarmHelper = new AlarmHelper(context);
        alarmHelper.scheduleExactAlarm();

        // setup cache-database
        Room.databaseBuilder(context.getApplicationContext(), GtpDatabase.class, "gtp-database").build();

        Log.v("fetch", "Working...");
        //testPush("Test", "Time: " + (new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())));
        if (SettingsStore.getInstance(context).isPush()) {
            CoverPlanDao dao = GtpDatabaseSingleton.getInstance(context).getCoverPlanDao();
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                List<CoverPlanRow> oldCoverPlan = dao.getCoverPlan();
                List<CoverPlanRow> oldRoomPlan = dao.getRoomChangePlan();
                CoverPlanFetcher.getInstance(context).getCoverPlan(null, coverPlan -> {
                    List<CoverPlanRow> newCoverPlan = coverPlan.getVertretung();
                    List<CoverPlanRow> newRoomPlan = coverPlan.getRaum();
                    for (CoverPlanRow row : oldCoverPlan) {
                        newCoverPlan.remove(row);
                    }
                    for (CoverPlanRow row : oldRoomPlan) {
                        newRoomPlan.remove(row);
                    }
                    // reverse order to show correct in push notifications
                    Collections.reverse(newCoverPlan);
                    Collections.reverse(newRoomPlan);

                    boolean groupSent = false;
                    for (CoverPlanRow row : newCoverPlan) {
                        Log.v("fetch", "New-Row: " + row.toString());
                        if (row.getGrade().startsWith(CoverPlanRow.BOLD_PREFIX)) {
                            if (!groupSent) {
                                groupNotification(false);
                                groupSent = true;
                            }
                            row.setGrade(row.getGrade().substring(3));
                            pushNotification("Neue Vertretung", row.toString(), false);
                        }
                        if (row.getTeacher().startsWith(CoverPlanRow.BOLD_PREFIX)) {
                            if (!groupSent) {
                                groupNotification(false);
                                groupSent = true;
                            }
                            row.setTeacher(row.getTeacher().substring(3));
                            pushNotification("Neue Vertretung", row.toString(), false);
                        }
                    }
                    groupSent = false;
                    for (CoverPlanRow row : newRoomPlan) {
                        if (row.getGrade().startsWith(CoverPlanRow.BOLD_PREFIX)) {
                            if (!groupSent) {
                                groupNotification(true);
                                groupSent = true;
                            }
                            row.setGrade(row.getGrade().substring(3));
                            pushNotification("Neue Raumänderung", row.toString(), true);
                        }
                        if (row.getTeacher().startsWith(CoverPlanRow.BOLD_PREFIX)) {
                            if (!groupSent) {
                                groupNotification(true);
                                groupSent = true;
                            }
                            row.setTeacher(row.getTeacher().substring(3));
                            pushNotification("Neue Raumänderung", row.toString(), true);
                        }
                    }
                });
            });
        }
    }

    private static final int COVERPLAN_GROUP_ID = 1;
    private static final int ROOMCHANGE_GROUP_ID = 2;
    private static int notificationId = 3;

    public void groupNotification(boolean roomChangeGroup) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.push_channel_id));
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, GtpActivity.class), PendingIntent.FLAG_IMMUTABLE))
                .setGroup(context.getString(roomChangeGroup ? R.string.push_group_id_roomchange : R.string.push_group_id_coverplan))
                .setGroupSummary(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(roomChangeGroup ? ROOMCHANGE_GROUP_ID : COVERPLAN_GROUP_ID, builder.build());
    }

    public void pushNotification(String title, String text, boolean roomChangeGroup) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.push_channel_id));
        builder.setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, GtpActivity.class), PendingIntent.FLAG_IMMUTABLE))
                .setGroup(context.getString(roomChangeGroup ? R.string.push_group_id_roomchange : R.string.push_group_id_coverplan))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setPriority(NotificationManager.IMPORTANCE_HIGH);
        Log.v("fetch", "Sent push with title " + title + " & text " + text);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationId++, builder.build());
    }

    public void testPush(String title, String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.push_channel_id));
        builder.setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, GtpActivity.class), PendingIntent.FLAG_IMMUTABLE))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setPriority(NotificationManager.IMPORTANCE_HIGH);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationId++, builder.build());
    }
}
