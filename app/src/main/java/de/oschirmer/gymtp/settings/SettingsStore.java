package de.oschirmer.gymtp.settings;

import android.content.Context;
import android.content.SharedPreferences;

import de.oschirmer.gymtp.R;

public class SettingsStore {

    private static SettingsStore instance;

    private SharedPreferences preferences;
    public String filterStateKey;
    public String filterTeacherStateKey;
    public String filterTeacherNameKey;
    public String filterGradeKey;

    private SettingsStore(Context context) {
        load(context);
    }

    public static SettingsStore getInstance(Context context) {
        if(instance == null) {
            instance = new SettingsStore(context);
        }
        return instance;
    }

     private void load(Context context) {
         preferences = context.getSharedPreferences(context.getString(R.string.settings_file_key), context.MODE_PRIVATE);

         filterStateKey = context.getString(R.string.saved_filter_state);
         filterTeacherStateKey = context.getString(R.string.saved_filter_teacher_state);
         filterTeacherNameKey = context.getString(R.string.saved_filter_teacher_name);
         filterGradeKey = context.getString(R.string.saved_filter_grade);
     }

     public SharedPreferences getPrefs() {
        return preferences;
     }

    public boolean isFilter() {
        return getPrefs().getBoolean(filterStateKey, false);
    }

    public boolean isTeacher() {
        return getPrefs().getBoolean(filterTeacherStateKey, false);
    }

    public int getGrade() {
        return getPrefs().getInt(filterGradeKey, 7);
    }

    public String getTeacherName() {
        return getPrefs().getString(filterTeacherNameKey, "Mustermann");
    }
}
