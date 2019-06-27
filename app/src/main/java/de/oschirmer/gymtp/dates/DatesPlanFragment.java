package de.oschirmer.gymtp.dates;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;

import de.oschirmer.gymtp.R;

public class DatesPlanFragment extends Fragment {

    private DatesPlanTableAdapter datesPlanTableAdapter;
    private ProgressBar loading;

    public DatesPlanFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dates_plan, container, false);

        datesPlanTableAdapter = new DatesPlanTableAdapter(getContext(), view.findViewById(R.id.list_dates_tables), new ArrayList<>());
        loading = view.findViewById(R.id.loading_dates);

        update();

        return view;
    }

    public void update() {
        // prevent update error when changing orientation - maybe last call of previous pagechangelistener?
        if(loading == null) {
            return;
        }
        loading.setVisibility(View.VISIBLE);
        DatesPlanFetcher.getInstance().getDatesPlan(plan -> {
            getActivity().runOnUiThread(() -> {
                loading.setVisibility(View.GONE);
                datesPlanTableAdapter.update(plan);
            });
        });
    }

}
