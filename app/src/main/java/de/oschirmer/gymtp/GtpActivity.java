package de.oschirmer.gymtp;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import de.oschirmer.gymtp.coverplan.CoverPlanFragment;
import de.oschirmer.gymtp.coverplan.fetch.FetchAlarmReceiver;
import de.oschirmer.gymtp.dates.DatesPlanFragment;
import de.oschirmer.gymtp.settings.SettingsFragment;
import de.oschirmer.gymtp.settings.SettingsStore;

public class GtpActivity extends AppCompatActivity {

    private SectionsPagerAdapter sectionsPagerAdapter;
    private CoverPlanFragment coverPlanFragment;
    private DatesPlanFragment datesPlanFragment;
    private SettingsFragment settingsFragment;
    private ViewPager viewPager;
    public String htmlDateParam;

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

        // Load specific date, when opened via URI
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        if (action == Intent.ACTION_VIEW) {
            htmlDateParam = "?d=" + data.getQueryParameter("d");
        }

        // register push-notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.push_channel_name);
            String description = getString(R.string.push_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.push_channel_id), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // remove existing notifications
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        // start background fetch alarm for coverplan etc
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, FetchAlarmReceiver.class);
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(getApplicationContext(), FetchAlarmReceiver.ALARM_ID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + FetchAlarmReceiver.FETCH_TIME, pendingAlarmIntent);
        } else {
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + FetchAlarmReceiver.FETCH_TIME, pendingAlarmIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
