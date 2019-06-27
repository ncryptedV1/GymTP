package de.oschirmer.gymtp.dates;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.List;

import de.oschirmer.gymtp.R;
import de.oschirmer.gymtp.dates.holder.DatesPlanTable;

public class DatesPlanTableAdapter {

    private Context context;
    private TableLayout tableLayout;
    private List<DatesPlanTable> tables;

    public DatesPlanTableAdapter(Context context, TableLayout tableLayout, List<DatesPlanTable> tables) {
        this.context = context;
        this.tableLayout = tableLayout;
        this.tables = tables;
    }

    public int getCount() {
        return tables.size();
    }

    public DatesPlanTable getItem(int position) {
        return tables.get(position);
    }

    public View getView(int position) {
        DatesPlanTableViewHolder holder;
        DatesPlanTable datesPlanTable = getItem(position);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.dates_plan_table, null);
        holder = new DatesPlanTableViewHolder(view);

        holder.heading.setText(datesPlanTable.getHeading());
        new DatesPlanRowAdapter(context, holder.rows, datesPlanTable.getRows()).update(datesPlanTable);

        return view;
    }

    public void update(DatesPlan datesPlan) {
        this.tables = datesPlan.getTables();
        tableLayout.removeAllViews();
        for (int i = 0; i < getCount(); i++) {
            tableLayout.addView(getView(i));
        }
    }

    public class DatesPlanTableViewHolder {
        private TextView heading;
        private TableLayout rows;

        public DatesPlanTableViewHolder(View view) {
            this.heading = view.findViewById(R.id.heading_dates);
            this.rows = view.findViewById(R.id.list_dates);
        }
    }

}
