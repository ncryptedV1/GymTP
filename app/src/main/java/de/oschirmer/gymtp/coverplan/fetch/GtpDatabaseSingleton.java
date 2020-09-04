package de.oschirmer.gymtp.coverplan.fetch;

import android.content.Context;

import androidx.room.Room;

public class GtpDatabaseSingleton {

    private static GtpDatabaseSingleton instance;
    private Context context;
    private GtpDatabase gtpDatabase;

    private GtpDatabaseSingleton(Context context) {
        this.context = context;
        gtpDatabase = Room.databaseBuilder(context.getApplicationContext(), GtpDatabase.class, "gtp-database").build();
    }

    public static GtpDatabaseSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new GtpDatabaseSingleton(context);
        }
        return instance;
    }

    public CoverPlanDao getCoverPlanDao() {
        return gtpDatabase.coverPlanDao();
    }
}
