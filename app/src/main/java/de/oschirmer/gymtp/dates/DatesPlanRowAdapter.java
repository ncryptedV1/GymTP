package de.oschirmer.gymtp.dates;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import de.oschirmer.gymtp.GtpActivity;
import de.oschirmer.gymtp.R;
import de.oschirmer.gymtp.dates.holder.DatesPlanRow;
import de.oschirmer.gymtp.dates.holder.DatesPlanTable;

public class DatesPlanRowAdapter {

    private Context context;
    private TableLayout tableLayout;
    private List<DatesPlanRow> rows;

    public DatesPlanRowAdapter(Context context, TableLayout tableLayout, List<DatesPlanRow> rows) {
        this.context = context;
        this.tableLayout = tableLayout;
        this.rows = rows;
    }

    public int getCount() {
        return rows.size();
    }

    public DatesPlanRow getItem(int position) {
        return rows.get(position);
    }

    public View getView(int position) {
        DatesPlanRowViewHolder holder;
        DatesPlanRow datesPlanRow = getItem(position);

        TableRow tableRow = new TableRow(context);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        tableRow.setLayoutParams(layoutParams);

        List<TextView> textViews = new ArrayList<>();
        for (int i = 0; i < datesPlanRow.getColumnCount(); i++) {
            TextView textView = new TextView(context);
            textView.setTypeface(Typeface.SANS_SERIF);
            textView.setTextColor(Color.BLACK);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            layoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            textView.setLayoutParams(layoutParams);

            tableRow.addView(textView);
            textViews.add(textView);
        }
        holder = new DatesPlanRowViewHolder(textViews);

        if (position % 2 == 0) {
            tableRow.setBackgroundColor(ContextCompat.getColor(context, R.color.tableRowAlternate));
        } else {
            tableRow.setBackgroundColor(ContextCompat.getColor(context, R.color.tableRowNormal));
        }

        for (int i = 0; i < datesPlanRow.getColumnCount(); i++) {
            TextView textView = holder.textViews.get(i);
            textView.setText(datesPlanRow.getItems().get(i));
            ViewGroup.LayoutParams lp = textView.getLayoutParams();
            if (lp instanceof ViewGroup.MarginLayoutParams) {
                if (i == 0) {
                    ((ViewGroup.MarginLayoutParams) lp).leftMargin = GtpActivity.dpToPx(context, 3);
                } else if (i == datesPlanRow.getColumnCount() - 1) {
                    ((ViewGroup.MarginLayoutParams) lp).rightMargin = GtpActivity.dpToPx(context, 3);
                }
                //... etc
            }
        }

        return tableRow;
    }

    public void update(DatesPlanTable tables) {
        this.rows = tables.getRows();
        this.tableLayout.removeAllViews();
        for (int i = 0; i < getCount(); i++) {
            tableLayout.addView(getView(i));
        }
    }

    public class DatesPlanRowViewHolder {
        private List<TextView> textViews;

        public DatesPlanRowViewHolder(List<TextView> textViews) {
            this.textViews = textViews;
        }
    }

}
