package de.oschirmer.gymtp.coverplan;

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

import de.oschirmer.gymtp.coverplan.holder.CoverPlan;
import de.oschirmer.gymtp.coverplan.holder.CoverPlanRow;

public class CoverPlanFetcher {

    private static CoverPlanFetcher instance;
    private static Context context;
    private static ExecutorService pool = Executors.newCachedThreadPool();

    private CoverPlan coverPlanCache;
    private String prevUrl;
    private String todayUrl;
    private String nextUrl;
    private long lastFetched;
    private long delay = 30 * 60 * 1000;
    private static final String url = "http://www.gymnasium-templin.de/typo3/vertretung/index13mob.php";

    private CoverPlanFetcher() {
    }

    public static void setContext(Context context) {
        CoverPlanFetcher.context = context;
    }

    public static CoverPlanFetcher getInstance() {
        if (instance == null) {
            instance = new CoverPlanFetcher();
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
        url = url == null ? CoverPlanFetcher.url : CoverPlanFetcher.url + url;
        if (!isOutdated()) {
            consumer.accept(coverPlanCache);
            return;
        }

        String finalUrl = url;
        pool.submit(() -> {
            try {
                coverPlanCache = new CoverPlan();

                Document doc = Jsoup.connect(finalUrl).get();
                coverPlanCache.setDate(doc.selectFirst("h3").text().substring(20));

                // extrahiere Vertretung
                Element table = doc.selectFirst("table[class=vertretung]");
                Elements tableHeader = table.select("thead > tr > th");
                coverPlanCache.getVertretung().add(new CoverPlanRow(tableHeader));
                Elements rows = table.select("tbody > tr");
                for (Element row : rows) {
                    Elements cells = row.select("td");
                    if (cells.size() == 6) {
                        coverPlanCache.getVertretung().add(new CoverPlanRow(cells));
                    } else {
                        coverPlanCache.getVertretung().add(new CoverPlanRow(cells.get(0).text(), "", "", "", "", ""));
                    }
                }

                // extrahiere Raumänderungen
                table = doc.selectFirst("table[class=raum]");
                tableHeader = table.select("thead > tr > th");
                coverPlanCache.getRaum().add(new CoverPlanRow(tableHeader));
                rows = table.select("tbody > tr");
                for (Element row : rows) {
                    Elements cells = row.select("td");
                    if (cells.size() == 6) {
                        coverPlanCache.getRaum().add(new CoverPlanRow(cells));
                    } else {
                        coverPlanCache.getRaum().add(new CoverPlanRow(cells.get(0).text(), "", "", "", "", ""));
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

                lastFetched = System.currentTimeMillis();
                consumer.accept(coverPlanCache);
            } catch (IOException e) {
                Toast.makeText(context, "Keine Internet-Verbindung!", Toast.LENGTH_SHORT);
                e.printStackTrace();
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
