package de.oschirmer.gymtp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import de.oschirmer.gymtp.coverplan.CoverPlanFragment;
import de.oschirmer.gymtp.dates.DatesPlanFragment;
import de.oschirmer.gymtp.settings.SettingsFragment;
import de.oschirmer.gymtp.settings.SettingsStore;
import de.oschirmer.gymtp.utils.AlarmHelper;

public class GtpActivity extends AppCompatActivity {

    private SectionsPagerAdapter sectionsPagerAdapter;
    private CoverPlanFragment coverPlanFragment;
    private DatesPlanFragment datesPlanFragment;
    private SettingsFragment settingsFragment;
    private ViewPager viewPager;
    public String htmlDateParam;
    private AlarmHelper alarmHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gtp);

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
                    coverPlanFragment.specific(null);
                } else if (position == 1) {
                    datesPlanFragment.update();
                }
            }
        };
        viewPager.addOnPageChangeListener(pageChangeListener);

        // Add Tabs for easier identifying
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        // load specific date, when opened via URI
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        if (Objects.equals(action, Intent.ACTION_VIEW) && data != null) {
            htmlDateParam = "?d=" + data.getQueryParameter("d");
        }

        // register push-notification channel
        CharSequence name = getString(R.string.push_channel_name);
        String description = getString(R.string.push_channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(getString(R.string.push_channel_id), name, importance);
        channel.setDescription(description);

        // register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        // remove existing notifications
        notificationManager.cancelAll();

        // initialize and check for exact alarm permission
        alarmHelper = new AlarmHelper(this);
        alarmHelper.checkAndRequestExactAlarmPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check again if permission is granted after returning from settings
        if (alarmHelper != null) {
            alarmHelper.checkAndRequestExactAlarmPermission();
        }

        SettingsStore store = SettingsStore.getInstance(this);
        store.getPrefs().edit().putBoolean(store.activityStateKey, true).apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SettingsStore store = SettingsStore.getInstance(this);
        store.getPrefs().edit().putBoolean(store.activityStateKey, false).apply();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final String[] tabTitles = {"Vertretungsplan", "Termine", "Einstellungen"};

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
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
