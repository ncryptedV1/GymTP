package de.oschirmer.gymtp.coverplan.fetch;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import de.oschirmer.gymtp.coverplan.holder.CoverPlan;
import de.oschirmer.gymtp.coverplan.holder.CoverPlanRow;
import de.oschirmer.gymtp.settings.SettingsStore;

public class CoverPlanFetcher {

    private static CoverPlanFetcher instance;
    private static ExecutorService pool = Executors.newCachedThreadPool();
    private Context context;
    private SettingsStore settingsStore;

    private CoverPlan coverPlanCache;
    private String prevUrl;
    private String todayUrl;
    private String nextUrl;
    private long lastFetched;
    private long delay = FetchAlarmReceiver.FETCH_TIME;
    private LinkedList<Request> queue = new LinkedList<>();
    private boolean fetching = false;
    private static final String url = "http://www.gymnasium-templin.de/typo3/vertretung/index13mob.php";


    private CoverPlanFetcher(Context context) {
        this.context = context;
        this.settingsStore = SettingsStore.getInstance(context);
    }

    public static CoverPlanFetcher getInstance(Context context) {
        if (instance == null) {
            instance = new CoverPlanFetcher(context);
        }
        return instance;
    }

    public void getPrevCoverPlan(Consumer<CoverPlan> consumer) {
        invalidateCache();
        getCoverPlan(prevUrl, consumer);
    }

    public void getTodayCoverPlan(Consumer<CoverPlan> consumer) {
        invalidateCache();
        getCoverPlan(todayUrl, consumer);
    }

    public void getNextCoverPlan(Consumer<CoverPlan> consumer) {
        invalidateCache();
        getCoverPlan(nextUrl, consumer);
    }

    public void getCoverPlan(String url, Consumer<CoverPlan> consumer) {
        // queue requests whilst currently fetching
        if (fetching) {
            queue.add(new Request(consumer, url));
            return;
        }

        url = url == null ? CoverPlanFetcher.url : CoverPlanFetcher.url + url;
        if (!isOutdated()) {
            consumer.accept(coverPlanCache);
            return;
        }

        fetching = true;
        String finalUrl = url;
        pool.submit(() -> {
            try {
                coverPlanCache = new CoverPlan(context);

                Document doc = Jsoup.connect(finalUrl).get();
                coverPlanCache.setDate(doc.selectFirst("h3").text().substring(20));

                // extrahiere Vertretung
                Element table = doc.selectFirst("table[class=vertretung]");
                Elements tableHeader = table.select("thead > tr > th");
                coverPlanCache.getVertretung().add(new CoverPlanRow(false, tableHeader));
                Elements rows = table.select("tbody > tr");
                for (Element row : rows) {
                    Elements cells = row.select("td");
                    if (cells.size() == 6) {
                        coverPlanCache.getVertretung().add(new CoverPlanRow(false, cells));
                    } else {
                        coverPlanCache.getVertretung().add(new CoverPlanRow(false, cells.get(0).text(), "", "", "", "", ""));
                    }
                }

                // extrahiere Raumänderungen
                table = doc.selectFirst("table[class=raum]");
                tableHeader = table.select("thead > tr > th");
                coverPlanCache.getRaum().add(new CoverPlanRow(true, tableHeader));
                rows = table.select("tbody > tr");
                for (Element row : rows) {
                    Elements cells = row.select("td");
                    if (cells.size() == 6) {
                        coverPlanCache.getRaum().add(new CoverPlanRow(true, cells));
                    } else {
                        coverPlanCache.getRaum().add(new CoverPlanRow(true, cells.get(0).text(), "", "", "", "", ""));
                    }
                }

                // extrahiere Anmerkungen
                Element divAnmerkung = doc.selectFirst("div[class=annotationv]");
                if (divAnmerkung != null) {
                    coverPlanCache.setAnmerkung(divAnmerkung.html().replace("<br> ", ""));
                } else {
                    coverPlanCache.setAnmerkung("");
                }

                // extrahiere URLs für Datums-Navigations Buttons
                prevUrl = doc.select("#nav_main > ul > li").get(0).selectFirst("a").attr("href");
                todayUrl = doc.select("#nav_main > ul > li").get(1).selectFirst("a").attr("href");
                nextUrl = doc.select("#nav_main > ul > li").get(2).selectFirst("a").attr("href");

                coverPlanCache.filterSort();
                if (finalUrl.equals(CoverPlanFetcher.url)) {
                    CoverPlanDao dao = GtpDatabaseSingleton.getInstance(context).getCoverPlanDao();
                    dao.clearTable();
                    dao.insertAll(coverPlanCache.getVertretung());
                    dao.insertAll(coverPlanCache.getRaum());
                }

                lastFetched = System.currentTimeMillis();
                consumer.accept(coverPlanCache);

                // trigger next request from queue
                Request request = queue.poll();
                if (request != null) {
                    getCoverPlan(request.getUrl(), request.getConsumer());
                }

                fetching = false;
            } catch (IOException e) {
                if (settingsStore.isActivityShown()) {
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, "Fehler beim Abrufen der Webseite:\n" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show());
                }
                e.printStackTrace();
                fetching = false;
            }
        });
    }

    private void invalidateCache() {
        lastFetched = 0;
    }

    public boolean isOutdated() {
        return System.currentTimeMillis() - lastFetched >= delay;
    }
}
