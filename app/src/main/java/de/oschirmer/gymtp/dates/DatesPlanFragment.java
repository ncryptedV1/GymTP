package de.oschirmer.gymtp.dates;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import de.oschirmer.gymtp.R;

public class DatesPlanFragment extends Fragment {

    private DatesPlanTableAdapter datesPlanTableAdapter;
    private ProgressBar loading;
    private DatesPlanFetcher fetcher;
    private TextView notAvailableTextView;

    public DatesPlanFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dates_plan, container, false);

        datesPlanTableAdapter = new DatesPlanTableAdapter(getContext(), view.findViewById(R.id.list_dates_tables), new ArrayList<>());
        loading = view.findViewById(R.id.loading_dates);
        fetcher = DatesPlanFetcher.getInstance(view.getContext());
        notAvailableTextView = view.findViewById(R.id.dates_not_available);

        update();

        return view;
    }

    public void update() {
        // prevent update error when changing orientation - maybe last call of previous pagechangelistener?
        if (loading == null) {
            return;
        }
        loading.setVisibility(View.VISIBLE);
        fetcher.getDatesPlan(plan -> getActivity().runOnUiThread(() -> {
            loading.setVisibility(View.GONE);
            datesPlanTableAdapter.update(plan);
            if (plan.hasFetchingFailed()) {
                notAvailableTextView.setVisibility(View.VISIBLE);
            } else {
                notAvailableTextView.setVisibility(View.GONE);
            }
        }));
    }

}
