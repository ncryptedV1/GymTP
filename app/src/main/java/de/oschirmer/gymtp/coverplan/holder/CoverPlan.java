package de.oschirmer.gymtp.coverplan.holder;

import java.util.ArrayList;
import java.util.List;

public class CoverPlan {

    private String date;
    private List<CoverPlanRow> vertretung;
    private List<CoverPlanRow> raum;
    private String anmerkung;

    public CoverPlan() {
        date = null;
        vertretung = new ArrayList<>();
        raum = new ArrayList<>();
        anmerkung = null;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public List<CoverPlanRow> getVertretung() {
        return vertretung;
    }

    public List<CoverPlanRow> getRaum() {
        return raum;
    }

    public String getAnmerkung() {
        return anmerkung;
    }

    public void setAnmerkung(String anmerkung) {
        this.anmerkung = anmerkung;
    }
}
