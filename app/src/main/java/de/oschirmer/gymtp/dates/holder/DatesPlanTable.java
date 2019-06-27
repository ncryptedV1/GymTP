package de.oschirmer.gymtp.dates.holder;

import java.util.ArrayList;
import java.util.List;

public class DatesPlanTable {

    private String heading;
    private List<DatesPlanRow> rows = new ArrayList<>();

    public DatesPlanTable(String heading) {
        this.heading = heading;
    }

    public String getHeading() {
        return heading;
    }

    public List<DatesPlanRow> getRows() {
        return rows;
    }
}
