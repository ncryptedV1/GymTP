package de.oschirmer.gymtp.dates;

import android.content.Context;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import de.oschirmer.gymtp.dates.holder.DatesPlanRow;
import de.oschirmer.gymtp.dates.holder.DatesPlanTable;

public class DatesPlanFetcher {

    private static DatesPlanFetcher instance;
    private static Context context;
    private static ExecutorService pool = Executors.newCachedThreadPool();

    private DatesPlan datesCache;
    private static final String url = "http://wp.gymnasium-templin.de/startseite/termine";

    private DatesPlanFetcher() {
    }

    public static void setContext(Context context) {
        DatesPlanFetcher.context = context;
    }

    public static DatesPlanFetcher getInstance() {
        if (instance == null) {
            instance = new DatesPlanFetcher();
        }
        return instance;
    }

    public void getDatesPlan(Consumer<DatesPlan> consumer) {
        if (datesCache != null) {
            consumer.accept(datesCache);
            return;
        }

        String finalUrl = url;
        pool.submit(() -> {
            try {
                datesCache = new DatesPlan();

                Document doc = Jsoup.connect(finalUrl).get();

                // extrahiere Überschriften
                Elements headings = doc.select("h3").not("h3#site-title");
                for (Element heading : headings) {
                    datesCache.getTables().add(new DatesPlanTable(heading.text()));
                }

                // extrahiere Tabellen
                Elements tables = doc.select("table");
                for (int i = 0; i < tables.size(); i++) {
                    DatesPlanTable tableHolder = datesCache.getTables().get(i);
                    Element table = tables.get(i);

                    Element header = table.selectFirst("thead");
                    Elements bodyRows = table.selectFirst("tbody").select("tr");
                    if (header != null) {
                        Elements headerItems = header.selectFirst("tr").select("th");
                        tableHolder.getRows().add(new DatesPlanRow(headerItems));
                    }
                    for (Element bodyRow : bodyRows) {
                        tableHolder.getRows().add(new DatesPlanRow(bodyRow.select("td")));
                    }
                }

                consumer.accept(datesCache);
            } catch (IOException e) {
                Toast.makeText(context, "Keine Internet-Verbindung!", Toast.LENGTH_SHORT);
                e.printStackTrace();
            }
        });
    }

}
