package de.oschirmer.gymtp.coverplan.fetch;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import de.oschirmer.gymtp.coverplan.holder.CoverPlanRow;

@Database(entities = {CoverPlanRow.class}, version = 1, exportSchema = false)
public abstract class GtpDatabase extends RoomDatabase {
    public abstract CoverPlanDao coverPlanDao();
}
