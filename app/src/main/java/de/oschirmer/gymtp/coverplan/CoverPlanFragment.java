package de.oschirmer.gymtp.coverplan;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import de.oschirmer.gymtp.R;
import de.oschirmer.gymtp.coverplan.holder.CoverPlan;

public class CoverPlanFragment extends Fragment {

    private View view;
    private TextView date;
    private CoverPlanTableAdapter coverPlanTableAdapter;
    private RoomTableAdapter roomTableAdapter;
    private TextView anmerkung;
    private ProgressBar loading;

    public CoverPlanFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cover_plan, container, false);

        // setze Variablen auf Instanzen der View
        date = view.findViewById(R.id.date);
        coverPlanTableAdapter = new CoverPlanTableAdapter(getContext(), view.findViewById(R.id.list_vertretung), new ArrayList<>());
        roomTableAdapter = new RoomTableAdapter(getContext(), view.findViewById(R.id.list_raum), new ArrayList<>());
        anmerkung = view.findViewById(R.id.anmerkung);
        //anmerkung.setBackgroundColor(getContext().getColor(R.color.tableRowAlternate));
        loading = view.findViewById(R.id.loading_cover_plan);

        // setze Datums-Navigations Button Listener
        view.findViewById(R.id.yesterday).setOnClickListener(v -> previous());
        view.findViewById(R.id.today).setOnClickListener(v -> today());
        view.findViewById(R.id.tomorrow).setOnClickListener(v -> next());

        updateCurrent();

        return view;
    }

    public void updateCurrent() {
        loading.setVisibility(View.VISIBLE);
        CoverPlanFetcher.getInstance().getCoverPlan(null, plan -> {
            update(plan);
        });
    }

    public void previous() {
        if (loading.getVisibility() == View.VISIBLE) {
            return;
        }
        loading.setVisibility(View.VISIBLE);
        CoverPlanFetcher.getInstance().getPrevCoverPlan(plan -> {
            update(plan);
        });
    }

    public void today() {
        if (loading.getVisibility() == View.VISIBLE) {
            return;
        }
        loading.setVisibility(View.VISIBLE);
        CoverPlanFetcher.getInstance().getTodayCoverPlan(plan -> {
            update(plan);
        });
    }

    public void next() {
        if (loading.getVisibility() == View.VISIBLE) {
            return;
        }
        loading.setVisibility(View.VISIBLE);
        CoverPlanFetcher.getInstance().getNextCoverPlan(plan -> {
            update(plan);
        });
    }

    private void update(CoverPlan plan) {
        getActivity().runOnUiThread(() -> {
            coverPlanTableAdapter.update(plan.getVertretung());
            roomTableAdapter.update(plan.getRaum());
            date.setText(plan.getDate());
            anmerkung.setText(plan.getAnmerkung());
            loading.setVisibility(View.GONE);
        });
    }

}
