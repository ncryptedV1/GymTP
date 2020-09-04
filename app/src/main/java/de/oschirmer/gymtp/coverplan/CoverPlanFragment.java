package de.oschirmer.gymtp.coverplan;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import de.oschirmer.gymtp.GtpActivity;
import de.oschirmer.gymtp.R;
import de.oschirmer.gymtp.coverplan.fetch.CoverPlanFetcher;
import de.oschirmer.gymtp.coverplan.holder.CoverPlan;

public class CoverPlanFragment extends Fragment {

    private View view;
    private TextView date;
    private CoverPlanTableAdapter coverPlanTableAdapter;
    private RoomChangeTableAdapter roomChangeTableAdapter;
    private TextView anmerkung;
    private ProgressBar loading;

    private CoverPlanFetcher fetcher;

    public CoverPlanFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cover_plan, container, false);

        // setze Variablen auf Instanzen der View
        date = view.findViewById(R.id.date);
        coverPlanTableAdapter = new CoverPlanTableAdapter(getContext(), view.findViewById(R.id.list_vertretung), new ArrayList<>());
        roomChangeTableAdapter = new RoomChangeTableAdapter(getContext(), view.findViewById(R.id.list_raum), new ArrayList<>());
        anmerkung = view.findViewById(R.id.anmerkung);
        //anmerkung.setBackgroundColor(getContext().getColor(R.color.tableRowAlternate));
        loading = view.findViewById(R.id.loading_cover_plan);

        // setze Datums-Navigations Button Listener
        view.findViewById(R.id.yesterday).setOnClickListener(v -> previous());
        view.findViewById(R.id.today).setOnClickListener(v -> today());
        view.findViewById(R.id.tomorrow).setOnClickListener(v -> next());

        fetcher = CoverPlanFetcher.getInstance(view.getContext());

        String htmlDateParam = ((GtpActivity) getActivity()).htmlDateParam;
        if (htmlDateParam != null) {
            if (isValidDateParam(htmlDateParam)) {
                specific(htmlDateParam);
            } else {
                specific(null);
            }
            ((GtpActivity) getActivity()).htmlDateParam = null;
        } else {
            specific(null);
        }

        return view;
    }

    public void specific(String htmlDateParam) {
        loading.setVisibility(View.VISIBLE);
        fetcher.getCoverPlan(htmlDateParam, this::update);
    }

    public void previous() {
        if (loading.getVisibility() == View.VISIBLE) {
            return;
        }
        loading.setVisibility(View.VISIBLE);
        fetcher.getPrevCoverPlan(this::update);
    }

    public void today() {
        if (loading.getVisibility() == View.VISIBLE) {
            return;
        }
        loading.setVisibility(View.VISIBLE);
        fetcher.getTodayCoverPlan(this::update);
    }

    public void next() {
        if (loading.getVisibility() == View.VISIBLE) {
            return;
        }
        loading.setVisibility(View.VISIBLE);
        fetcher.getNextCoverPlan(this::update);
    }

    private void update(CoverPlan plan) {
        getActivity().runOnUiThread(() -> {
            coverPlanTableAdapter.update(plan.getVertretung());
            roomChangeTableAdapter.update(plan.getRaum());
            date.setText(plan.getDate());
            anmerkung.setText(plan.getAnmerkung());
            loading.setVisibility(View.GONE);
        });
    }

    private static boolean isValidDateParam(String htmlDateParam) {
        return htmlDateParam.startsWith("?d=")
                && htmlDateParam.length() == 11
                && isInteger(htmlDateParam.substring(3))
                && Integer.parseInt(htmlDateParam.substring(3, 7)) > 2000
                && Integer.parseInt(htmlDateParam.substring(7, 9)) > 0
                && Integer.parseInt(htmlDateParam.substring(9, 11)) > 0;
    }

    private static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
}
