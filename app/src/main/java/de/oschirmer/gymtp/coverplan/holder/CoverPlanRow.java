package de.oschirmer.gymtp.coverplan.holder;

import org.jsoup.select.Elements;

public class CoverPlanRow {

    private String teacher;
    private String hour;
    private String subject;
    private String grade;
    private String room;
    private String notice;

    public static final String BOLD_PREFIX = "<b>";

    public CoverPlanRow(Elements elements) {
        this(elements.get(0).text(), elements.get(1).text(), elements.get(2).text(), elements.get(3).text(), elements.get(4).text(), elements.get(5).text());
    }

    public CoverPlanRow(String teacher, String hour, String subject, String grade, String room, String notice) {
        this.teacher = teacher;
        this.hour = hour;
        this.subject = subject;
        this.grade = grade;
        this.room = room;
        this.notice = notice;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getHour() {
        return hour;
    }

    public String getSubject() {
        return subject;
    }

    public String getGrade() {
        return grade;
    }

    public String getRoom() {
        return room;
    }

    public String getNotice() {
        return notice;
    }

    public void setTeacherBold(boolean bold) {
        if(bold && !teacher.startsWith(BOLD_PREFIX)) {
            teacher = BOLD_PREFIX + teacher;
        } else if(!bold && teacher.startsWith(BOLD_PREFIX)) {
            teacher = teacher.substring(3);
        }
    }

    public void setGradeBold(boolean bold) {
        if(bold && !grade.startsWith(BOLD_PREFIX)) {
            grade = BOLD_PREFIX + grade;
        } else if(!bold && grade.startsWith(BOLD_PREFIX)) {
            grade = grade.substring(3);
        }
    }
}
