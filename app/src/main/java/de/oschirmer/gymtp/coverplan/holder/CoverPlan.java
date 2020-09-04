package de.oschirmer.gymtp.coverplan.holder;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import de.oschirmer.gymtp.settings.SettingsStore;
import java9.util.Lists;

public class CoverPlan {

    private String date;
    private List<CoverPlanRow> vertretung;
    private List<CoverPlanRow> raum;
    private String anmerkung;

    private Context context;

    public CoverPlan(Context context) {
        date = null;
        vertretung = new ArrayList<>();
        raum = new ArrayList<>();
        anmerkung = null;

        this.context = context;
    }

    public void filterSort() {
        SettingsStore settings = SettingsStore.getInstance(context);
        if (settings.isFilter()) {
            boolean teacher = settings.isTeacher();
            int grade = settings.getGrade();
            String teacherName = settings.getTeacherName();
            Lists.sort(vertretung, (row1, row2) -> {
                int first;
                int second;
                if (teacher) {
                    String teacher1 = row1.getTeacher();
                    String teacher2 = row2.getTeacher();
                    first = teacher1.equals("Vertretung") ? 2 : (teacher1.contains(teacherName) ? 1 : 0);
                    second = teacher2.equals("Vertretung") ? 2 : (teacher2.contains(teacherName) ? 1 : 0);
                    row1.setTeacherBold(first == 1);
                    row2.setTeacherBold(second == 1);
                } else {
                    String grade1 = row1.getGrade();
                    String grade2 = row2.getGrade();
                    first = grade1.equals("Klasse") ? 2 : (grade1.contains(grade + "") ? 1 : 0);
                    second = grade2.equals("Klasse") ? 2 : (grade2.contains(grade + "") ? 1 : 0);
                    row1.setGradeBold(first == 1);
                    row2.setGradeBold(second == 1);
                }
                return Integer.compare(second, first);
            });
            Lists.sort(raum, (row1, row2) -> {
                int first;
                int second;
                if (teacher) {
                    String teacher1 = row1.getTeacher();
                    String teacher2 = row2.getTeacher();
                    first = teacher1.equals("Raumänderung") ? 2 : (teacher1.contains(teacherName) ? 1 : 0);
                    second = teacher2.equals("Raumänderung") ? 2 : (teacher2.contains(teacherName) ? 1 : 0);
                    row1.setTeacherBold(first == 1);
                    row2.setTeacherBold(second == 1);
                } else {
                    String grade1 = row1.getGrade();
                    String grade2 = row2.getGrade();
                    first = grade1.equals("Klasse") ? 2 : (grade1.contains(grade + "") ? 1 : 0);
                    second = grade2.equals("Klasse") ? 2 : (grade2.contains(grade + "") ? 1 : 0);
                    row1.setGradeBold(first == 1);
                    row2.setGradeBold(second == 1);
                }
                return Integer.compare(second, first);
            });
        }
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
