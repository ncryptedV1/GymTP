package de.oschirmer.gymtp.dates;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import de.oschirmer.gymtp.coverplan.fetch.Request;
import de.oschirmer.gymtp.dates.holder.DatesPlanRow;
import de.oschirmer.gymtp.dates.holder.DatesPlanTable;

public class DatesPlanFetcher {

    private static DatesPlanFetcher instance;
    private static ExecutorService pool = Executors.newCachedThreadPool();

    private DatesPlan datesCache;
    private LinkedList<Request> queue = new LinkedList<>();
    private boolean fetching = false;
    private static final String url = "http://wp.gymnasium-templin.de/startseite/termine";

    private DatesPlanFetcher() {
    }

    public static DatesPlanFetcher getInstance() {
        if (instance == null) {
            instance = new DatesPlanFetcher();
        }
        return instance;
    }

    public void getDatesPlan(Consumer<DatesPlan> consumer) {
        // queue requests whilst currently fetching
        if (fetching) {
            queue.add(new Request(consumer, null));
            return;
        }

        if (datesCache != null && !datesCache.hasFetchingFailed()) {
            consumer.accept(datesCache);
            return;
        }

        fetching = true;
        pool.submit(() -> {
            try {
                datesCache = new DatesPlan();

                Document doc = Jsoup.connect(url).get();

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

                // trigger next request from queue
                Request request = queue.poll();
                if (request != null) {
                    getDatesPlan(request.getConsumer());
                }

                datesCache.setFetchingFailed(false);
                consumer.accept(datesCache);
                fetching = false;
            } catch (IOException e) {
                /*if (settingsStore.isActivityShown()) {
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, "Fehler beim Abrufen der Webseite:\n" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show());
                }*/
                datesCache.setFetchingFailed(true);
                consumer.accept(datesCache);
                e.printStackTrace();
                fetching = false;
            }
        });
    }

}
