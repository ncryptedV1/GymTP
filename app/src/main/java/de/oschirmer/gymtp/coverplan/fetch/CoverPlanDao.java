package de.oschirmer.gymtp.coverplan.fetch;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import de.oschirmer.gymtp.coverplan.holder.CoverPlanRow;

@Dao
public interface CoverPlanDao {
    @Query("SELECT * FROM coverplanrow WHERE is_room_change = 0")
    List<CoverPlanRow> getCoverPlan();

    @Query("SELECT * FROM coverplanrow WHERE is_room_change = 1")
    List<CoverPlanRow> getRoomChangePlan();

    @Insert
    void insertAll(List<CoverPlanRow> rows);

    @Query("DELETE FROM coverplanrow")
    void clearTable();

}
