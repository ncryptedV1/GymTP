package de.oschirmer.gymtp.dates;

import java.util.ArrayList;
import java.util.List;

import de.oschirmer.gymtp.dates.holder.DatesPlanTable;

public class DatesPlan {

    private List<DatesPlanTable> tables;
    private boolean fetchingFailed = false;

    public DatesPlan() {
        tables = new ArrayList<>();
    }

    public List<DatesPlanTable> getTables() {
        return tables;
    }

    public boolean hasFetchingFailed() {
        return fetchingFailed;
    }

    public void setFetchingFailed(boolean fetchingFailed) {
        this.fetchingFailed = fetchingFailed;
    }
}
