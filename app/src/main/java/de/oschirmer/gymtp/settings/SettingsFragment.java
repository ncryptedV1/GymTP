package de.oschirmer.gymtp.settings;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.oschirmer.gymtp.R;

public class SettingsFragment extends Fragment {

    private Switch filterSwitch;
    private Switch pushSwitch;
    private Switch teacherSwitch;
    private TextView pupilGrade;
    private Spinner pupilGradeSpinner;
    private TextView teacherName;
    private EditText teacherNameField;

    private SettingsStore settingsStore;

    public SettingsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        settingsStore = SettingsStore.getInstance(getContext());

        filterSwitch = view.findViewById(R.id.filter_switch);
        filterSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> toggleFilter(isChecked));
        pushSwitch = view.findViewById(R.id.push_switch);
        pushSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> togglePush(isChecked));
        teacherSwitch = view.findViewById(R.id.teacher_switch);
        teacherSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> toggleTeacher(isChecked));
        pupilGrade = view.findViewById(R.id.pupil_grade);
        pupilGradeSpinner = view.findViewById(R.id.pupil_grade_spinner);
        pupilGradeSpinner.setAdapter(ArrayAdapter.createFromResource(getContext(), R.array.grades_array, R.layout.support_simple_spinner_dropdown_item));
        pupilGradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view == null) {
                    return;
                }
                int grade = Integer.parseInt(((TextView) view).getText().toString());
                settingsStore.getPrefs().edit().putInt(settingsStore.filterGradeKey, grade).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        teacherName = view.findViewById(R.id.teacher_name);
        teacherNameField = view.findViewById(R.id.teacher_name_field);
        teacherNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                settingsStore.getPrefs().edit().putString(settingsStore.filterTeacherNameKey, s.toString()).apply();
            }
        });

        loadSettings();

        return view;
    }

    private void loadSettings() {
        teacherNameField.setText(settingsStore.getTeacherName());
        pupilGradeSpinner.setSelection(((ArrayAdapter) pupilGradeSpinner.getAdapter()).getPosition("" + settingsStore.getGrade()));

        boolean teacher = settingsStore.isTeacher();
        teacherSwitch.setChecked(teacher);
        toggleTeacher(teacher);

        boolean filter = settingsStore.isFilter();
        filterSwitch.setChecked(filter);
        toggleFilter(filter);

        boolean push = filter && settingsStore.isPush();
        pushSwitch.setChecked(push);
        togglePush(push);
    }

    private void toggleFilter(boolean filter) {
        settingsStore.getPrefs().edit().putBoolean(settingsStore.filterStateKey, filter).apply();
        if (filter) {
            pushSwitch.setVisibility(View.VISIBLE);
            teacherSwitch.setVisibility(View.VISIBLE);
            teacherSwitch.setChecked(!teacherSwitch.isChecked());
            teacherSwitch.setChecked(!teacherSwitch.isChecked());
        } else {
            pushSwitch.setVisibility(View.GONE);
            togglePush(false);
            teacherSwitch.setVisibility(View.GONE);
            pupilGrade.setVisibility(View.GONE);
            pupilGradeSpinner.setVisibility(View.GONE);
            teacherName.setVisibility(View.GONE);
            teacherNameField.setVisibility(View.GONE);
        }
    }

    private void togglePush(boolean push) {
        settingsStore.getPrefs().edit().putBoolean(settingsStore.pushStateKey, push).apply();
    }

    private void toggleTeacher(boolean teacher) {
        settingsStore.getPrefs().edit().putBoolean(settingsStore.filterTeacherStateKey, teacher).apply();
        if (teacher) {
            teacherMode();
        } else {
            pupilMode();
        }
    }

    private void teacherMode() {
        if (filterSwitch.isChecked()) {
            pupilGrade.setVisibility(View.GONE);
            pupilGradeSpinner.setVisibility(View.GONE);
            teacherName.setVisibility(View.VISIBLE);
            teacherNameField.setVisibility(View.VISIBLE);
        }
    }

    private void pupilMode() {
        if (filterSwitch.isChecked()) {
            pupilGrade.setVisibility(View.VISIBLE);
            pupilGradeSpinner.setVisibility(View.VISIBLE);
            teacherName.setVisibility(View.GONE);
            teacherNameField.setVisibility(View.GONE);
        }
    }
}
