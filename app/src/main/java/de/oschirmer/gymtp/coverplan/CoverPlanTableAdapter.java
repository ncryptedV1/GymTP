package de.oschirmer.gymtp.coverplan;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.List;

import de.oschirmer.gymtp.R;
import de.oschirmer.gymtp.coverplan.holder.CoverPlanRow;
import de.oschirmer.gymtp.settings.SettingsStore;

class CoverPlanTableAdapter {

    private Context context;
    private TableLayout tableLayout;
    private List<CoverPlanRow> rows;

    public CoverPlanTableAdapter(Context context, TableLayout tableLayout, List<CoverPlanRow> rows) {
        this.context = context;
        this.tableLayout = tableLayout;
        this.rows = rows;
        filterSort();
    }

    public int getCount() {
        return rows.size();
    }

    public CoverPlanRow getItem(int position) {
        return rows.get(position);
    }

    public View getView(int position) {
        CoverPlanRowViewHolder holder;

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.cover_plan_row, null);
        holder = new CoverPlanRowViewHolder(view);

        if (position % 2 == 0) {
            view.setBackgroundColor(context.getColor(R.color.tableRowAlternate));
        } else {
            view.setBackgroundColor(context.getColor(R.color.tableRowNormal));
        }

        CoverPlanRow row = getItem(position);
        holder.first.setText(row.getHour() + "\n" + row.getSubject());
        if (row.getGrade().startsWith(CoverPlanRow.BOLD_PREFIX)) {
            holder.second.setText(row.getGrade().substring(3));
            holder.second.setTypeface(holder.second.getTypeface(), Typeface.BOLD);
        } else {
            holder.second.setText(row.getGrade());
        }
        if (row.getTeacher().startsWith(CoverPlanRow.BOLD_PREFIX)) {
            SpannableStringBuilder str = new SpannableStringBuilder(row.getTeacher().substring(3) + "\n" + row.getRoom());
            str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, row.getTeacher().length() - 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.third.setText(str);
        } else {
            holder.third.setText(row.getTeacher() + "\n" + row.getRoom());
        }
        holder.fourth.setText(row.getNotice());

        return view;
    }

    public void update(List<CoverPlanRow> rows) {
        this.rows = rows;
        filterSort();
        tableLayout.removeAllViews();
        for (int i = 0; i < getCount(); i++) {
            tableLayout.addView(getView(i));
        }
    }

    private void filterSort() {
        SettingsStore settings = SettingsStore.getInstance(context);
        if (settings.isFilter()) {
            boolean teacher = settings.isTeacher();
            int grade = settings.getGrade();
            String teacherName = settings.getTeacherName();
            rows.sort((row1, row2) -> {
                boolean first;
                boolean second;
                if (teacher) {
                    if(row1.getTeacher().equals("Vertretung") || row2.getTeacher().equals("Vertretung")) {
                        return 0;
                    }
                    first = row1.getTeacher().contains(teacherName);
                    second = row2.getTeacher().contains(teacherName);
                    row1.setTeacherBold(first);
                    row2.setTeacherBold(second);
                } else {
                    if(row1.getGrade().equals("Klasse") || row2.getGrade().equals("Klasse")) {
                        return 0;
                    }
                    first = row1.getGrade().contains(grade + "");
                    second = row2.getGrade().contains(grade + "");
                    row1.setGradeBold(first);
                    row2.setGradeBold(second);
                }
                return first && !second ? -1 : !first && second ? 1 : 0;
            });
        }
    }

    public class CoverPlanRowViewHolder {
        private TextView first;
        private TextView second;
        private TextView third;
        private TextView fourth;

        public CoverPlanRowViewHolder(View view) {
            first = view.findViewById(R.id.row_first);
            second = view.findViewById(R.id.row_second);
            third = view.findViewById(R.id.row_third);
            fourth = view.findViewById(R.id.row_fourth);
        }
    }

}
