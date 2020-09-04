package de.oschirmer.gymtp.coverplan.holder;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import org.jsoup.select.Elements;

@Entity(primaryKeys = {"is_room_change", "teacher", "hour", "subject", "grade", "room", "notice"})
public class CoverPlanRow {

    @ColumnInfo(name = "is_room_change")
    private boolean isRoomChange;

    @NonNull
    private String teacher;
    @NonNull
    private String hour;
    @NonNull
    private String subject;
    @NonNull
    private String grade;
    @NonNull
    private String room;
    @NonNull
    private String notice;

    @Ignore
    public static final String BOLD_PREFIX = "<b>";

    public CoverPlanRow(boolean isRoomChange, Elements elements) {
        this(isRoomChange, elements.get(0).text(), elements.get(1).text(), elements.get(2).text()
                , elements.get(3).text(), elements.get(4).text(), elements.get(5).text());
    }

    public CoverPlanRow(boolean isRoomChange, String teacher, String hour, String subject,
                        String grade, String room, String notice) {
        this.isRoomChange = isRoomChange;
        this.teacher = teacher;
        this.hour = hour;
        this.subject = subject;
        this.grade = grade;
        this.room = room;
        this.notice = notice;
    }

    public boolean isRoomChange() {
        return isRoomChange;
    }

    public void setRoomChange(boolean roomChange) {
        isRoomChange = roomChange;
    }

    @NonNull
    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(@NonNull String teacher) {
        this.teacher = teacher;
    }

    @NonNull
    public String getHour() {
        return hour;
    }

    public void setHour(@NonNull String hour) {
        this.hour = hour;
    }

    @NonNull
    public String getSubject() {
        return subject;
    }

    public void setSubject(@NonNull String subject) {
        this.subject = subject;
    }

    @NonNull
    public String getGrade() {
        return grade;
    }

    public void setGrade(@NonNull String grade) {
        this.grade = grade;
    }

    @NonNull
    public String getRoom() {
        return room;
    }

    public void setRoom(@NonNull String room) {
        this.room = room;
    }

    @NonNull
    public String getNotice() {
        return notice;
    }

    public void setNotice(@NonNull String notice) {
        this.notice = notice;
    }

    public void setTeacherBold(boolean bold) {
        if (bold && !teacher.startsWith(BOLD_PREFIX)) {
            teacher = BOLD_PREFIX + teacher;
        } else if (!bold && teacher.startsWith(BOLD_PREFIX)) {
            teacher = teacher.substring(3);
        }
    }

    public void setGradeBold(boolean bold) {
        if (bold && !grade.startsWith(BOLD_PREFIX)) {
            grade = BOLD_PREFIX + grade;
        } else if (!bold && grade.startsWith(BOLD_PREFIX)) {
            grade = grade.substring(3);
        }
    }

    public String toString() {
        return getHour() + " " + getSubject() + " | " + getGrade() + " | " + getTeacher() + " | " + getRoom() + " | " + getNotice();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CoverPlanRow)) {
            return false;
        }
        CoverPlanRow row = (CoverPlanRow) obj;
        if (row.isRoomChange == isRoomChange
                && row.teacher.equals(teacher)
                && row.hour.equals(hour)
                && row.subject.equals(subject)
                && row.grade.equals(grade)
                && row.room.equals(room)
                && row.notice.equals(notice)) {
            return true;
        }
        return false;
    }
}
