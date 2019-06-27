package de.oschirmer.gymtp.dates.holder;

import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class DatesPlanRow {

    private int columnCount;
    private List<String> items = new ArrayList<>();

    public DatesPlanRow(Elements elements) {
        columnCount = elements.size();
        for (int i = 0; i < columnCount; i++) {
            items.add(elements.get(i).text());
        }
    }

    public int getColumnCount() {
        return getItems().size();
    }

    public List<String> getItems() {
        return items;
    }
}
