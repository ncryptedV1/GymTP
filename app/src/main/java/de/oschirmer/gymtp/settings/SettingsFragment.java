package de.oschirmer.gymtp.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import de.oschirmer.gymtp.R;

public class SettingsFragment extends Fragment {

    private Switch filterSwitch;
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
        filterSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleFilter(isChecked);
            settingsStore.getPrefs().edit().putBoolean(settingsStore.filterStateKey, isChecked).apply();
        });
        teacherSwitch = view.findViewById(R.id.teacher_switch);
        teacherSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleTeacher(isChecked);
            settingsStore.getPrefs().edit().putBoolean(settingsStore.filterTeacherStateKey, isChecked).apply();
        });
        pupilGrade = view.findViewById(R.id.pupil_grade);
        pupilGradeSpinner = view.findViewById(R.id.pupil_grade_spinner);
        pupilGradeSpinner.setAdapter(ArrayAdapter.createFromResource(this.getContext(), R.array.grades_array, R.layout.support_simple_spinner_dropdown_item));
        pupilGradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view == null) {
                    return;
                }
                Integer grade = Integer.parseInt(((TextView) view).getText().toString());
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
        teacherNameField.setText(settingsStore.getPrefs().getString(settingsStore.filterTeacherNameKey, "Mustermann"));
        pupilGradeSpinner.setSelection(((ArrayAdapter) pupilGradeSpinner.getAdapter()).getPosition("" + settingsStore.getPrefs().getInt(settingsStore.filterGradeKey, 7)));
        boolean teacher = settingsStore.getPrefs().getBoolean(settingsStore.filterTeacherStateKey, false);
        teacherSwitch.setChecked(teacher);
        toggleTeacher(teacher);
        boolean filter = settingsStore.getPrefs().getBoolean(settingsStore.filterStateKey, false);
        filterSwitch.setChecked(filter);
        toggleFilter(filter);
    }

    private void toggleFilter(boolean filter) {
        if (filter) {
            teacherSwitch.setVisibility(View.VISIBLE);
            teacherSwitch.setChecked(!teacherSwitch.isChecked());
            teacherSwitch.setChecked(!teacherSwitch.isChecked());
        } else {
            teacherSwitch.setVisibility(View.GONE);
            pupilGrade.setVisibility(View.GONE);
            pupilGradeSpinner.setVisibility(View.GONE);
            teacherName.setVisibility(View.GONE);
            teacherNameField.setVisibility(View.GONE);
        }
    }

    private void toggleTeacher(boolean teacher) {
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
