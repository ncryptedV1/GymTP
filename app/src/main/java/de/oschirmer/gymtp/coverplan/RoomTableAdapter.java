package de.oschirmer.gymtp.coverplan;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.List;

import de.oschirmer.gymtp.R;
import de.oschirmer.gymtp.coverplan.holder.CoverPlanRow;

public class RoomTableAdapter {

    private Context context;
    private TableLayout tableLayout;
    private List<CoverPlanRow> rows;

    public RoomTableAdapter(Context context, TableLayout tableLayout, List<CoverPlanRow> rows) {
        this.context = context;
        this.tableLayout = tableLayout;
        this.rows = rows;
    }

    public int getCount() {
        return rows.size();
    }

    public CoverPlanRow getItem(int position) {
        return rows.get(position);
    }

    public View getView(int position) {
        RoomRowViewHolder holder;

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.cover_plan_row, null);
        holder = new RoomRowViewHolder(view);

        if (position % 2 == 0) {
            view.setBackgroundColor(context.getColor(R.color.tableRowAlternate));
        } else {
            view.setBackgroundColor(context.getColor(R.color.tableRowNormal));
        }

        CoverPlanRow row = getItem(position);
        holder.first.setText(row.getHour() + "\n" + row.getSubject());
        holder.second.setText(row.getGrade());
        holder.third.setText(row.getTeacher() + "\n" + row.getRoom());
        holder.fourth.setText(row.getNotice());

        return view;
    }

    public void update(List<CoverPlanRow> rows) {
        this.rows = rows;
        tableLayout.removeAllViews();
        for (int i = 0; i < getCount(); i++) {
            tableLayout.addView(getView(i));
        }
    }

    public class RoomRowViewHolder {
        private TextView first;
        private TextView second;
        private TextView third;
        private TextView fourth;

        public RoomRowViewHolder(View view) {
            first = view.findViewById(R.id.row_first);
            second = view.findViewById(R.id.row_second);
            third = view.findViewById(R.id.row_third);
            fourth = view.findViewById(R.id.row_fourth);
        }
    }
}
