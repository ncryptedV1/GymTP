package de.oschirmer.gymtp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewGroup;

import de.oschirmer.gymtp.coverplan.CoverPlanFetcher;
import de.oschirmer.gymtp.coverplan.CoverPlanFragment;
import de.oschirmer.gymtp.dates.DatesPlanFragment;
import de.oschirmer.gymtp.settings.SettingsFragment;
import de.oschirmer.gymtp.settings.SettingsStore;

public class GtpActivity extends AppCompatActivity {

    private SectionsPagerAdapter sectionsPagerAdapter;
    private CoverPlanFragment coverPlanFragment;
    private DatesPlanFragment datesPlanFragment;
    private SettingsFragment settingsFragment;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gtp);

        CoverPlanFetcher.setContext(getApplicationContext());

        // load settings
        SettingsStore.getInstance(this);

        // Create the adapter that will return a fragment for each of the sections
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                sectionsPagerAdapter.getItem(position);
                if (position == 0) {
                    coverPlanFragment.updateCurrent();
                } else if (position == 1) {
                    datesPlanFragment.update();
                }
            }
        };
        viewPager.addOnPageChangeListener(pageChangeListener);

        // Add Tabs for easier identifying
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final String[] tabTitles = {"Vertretungsplan", "Termine", "Einstellungen"};

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object fragment = super.instantiateItem(container, position);
            if (position == 0) {
                coverPlanFragment = (CoverPlanFragment) fragment;
            } else if (position == 1) {
                datesPlanFragment = (DatesPlanFragment) fragment;
            } else if (position == 2) {
                settingsFragment = (SettingsFragment) fragment;
            }
            return fragment;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (coverPlanFragment == null) {
                    coverPlanFragment = new CoverPlanFragment();
                }
                return coverPlanFragment;
            } else if (position == 1) {
                if (datesPlanFragment == null) {
                    datesPlanFragment = new DatesPlanFragment();
                }
                return datesPlanFragment;
            } else if (position == 2) {
                if (settingsFragment == null) {
                    settingsFragment = new SettingsFragment();
                }
                return settingsFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }
}
